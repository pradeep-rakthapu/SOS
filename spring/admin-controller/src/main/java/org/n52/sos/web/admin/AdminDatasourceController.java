/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.web.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.xmlbeans.XmlException;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.janmayen.Json;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.sos.ds.GeneralQueryDAO;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 *
 */
@Controller
public class AdminDatasourceController
        extends AbstractDatasourceController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminDatasourceController.class);

    private static final String ROWS = "rows";

    private static final String NAMES = "names";

    private static final String SUPPORTS_CLEAR = "supportsClear";

    private static final String SUPPORTS_DELETE_DELETED = "supportsDeleteDeleted";

    @Inject
    private Optional<GeneralQueryDAO> generalQueryDAO;

    @Inject
    private DecoderRepository decoderRepository;

    @Inject
    private RequestOperatorRepository requestOperatorRepository;

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE)
    public ModelAndView index() throws SQLException, OwsExceptionReport {
        Map<String, Object> model = Maps.newHashMap();
        model.put(SUPPORTS_CLEAR, getDatasource().supportsClear());
        model.put(SUPPORTS_DELETE_DELETED, generalQueryDAO != null);
        return new ModelAndView(ControllerConstants.Views.ADMIN_DATASOURCE, model);
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_EXECUTE, method = RequestMethod.POST)
    public String processQuery(@RequestBody String querySQL) {
        try {
            if (this.generalQueryDAO.isPresent()) {
                String q = URLDecoder.decode(querySQL, "UTF-8");
                LOG.info("Query: {}", q);
                GeneralQueryDAO.QueryResult rs = this.generalQueryDAO.get().query(q);
                ObjectNode j = Json.nodeFactory().objectNode();
                if (rs.getMessage() != null) {
                    j.put(rs.isError() ? "error" : "message", rs.getMessage());
                    return Json.print(j);
                }
                j.putArray(ROWS).addAll(Json.toJSON(rs.getColumnNames()));
                ArrayNode names = j.putArray(NAMES);
                for (GeneralQueryDAO.Row row : rs.getRows()) {
                    names.addArray().addAll(Json.toJSON(row.getValues()));
                }
                return Json.print(j);
            }
            return "No general query dao available!";
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Could not decode String", ex);
            return "Could not decode String: " + ex.getMessage();
        } catch (SQLException | IOException ex) {
            LOG.error("Query unsuccesfull.", ex);
            return "Query unsuccesful. Cause: " + ex.getMessage();
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnsupportedOperationException.class)
    public String onError(UnsupportedOperationException e) {
        return "The operation is not supported.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_CLEAR, method = RequestMethod.POST)
    public void clearDatasource() throws OwsExceptionReport, ConnectionProviderException {
        if (getDatasource().supportsClear()) {
            LOG.info("Clearing database contents by calling clear method.");
            getDatasource().clear(getSettings());
        } else {
            LOG.info("Clearing database contents by deleting and recreating the SOS database schema.");
            Map<String, Object> settings = getDatasource().parseDatasourceProperties(getSettings());
            getDatasource().dropSchema(settings);
            getDatasource().createSchema(settings);
        }
        updateCache();
    }

    @ResponseBody
    @ExceptionHandler(MissingServiceOperatorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String onConnectionMissingServiceOperatorException(MissingServiceOperatorException e) {
        return e.getMessage();
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_ADD_SAMPLEDATA, method = RequestMethod.POST)
    public String addSampledata(HttpServletRequest request) throws OwsExceptionReport, ConnectionProviderException,
            IOException, URISyntaxException, XmlException, MissingServiceOperatorException, DecodingException {

        boolean sampledataAdded = new SampleDataInserter(OwsServiceRequestContext.fromRequest(request),
                decoderRepository, requestOperatorRepository).insertSampleData();
        if (sampledataAdded) {
            updateCache();
        }
        return "OK";
    }

}
