/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.sqlite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.ogc.swes.OfferingExtensionKey;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.sos.config.SosActivationDao;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtension;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtensionKey;
import org.n52.sos.config.sqlite.entities.ObservationEncoding;
import org.n52.sos.config.sqlite.entities.ObservationEncodingKey;
import org.n52.sos.config.sqlite.entities.ProcedureEncoding;
import org.n52.sos.config.sqlite.entities.ProcedureEncodingKey;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SosSQLiteActivationDao
        extends SQLiteActivationDao
        implements SosActivationDao {

    // RESPONSE FORMAT
    @Override
    public void setResponseFormatStatus(ResponseFormatKey rfkt, boolean active) {
        setActive(ObservationEncoding.class, new ObservationEncoding(rfkt), active);
    }

    @Override
    public boolean isResponseFormatActive(ResponseFormatKey rfkt) {
        return isActive(ObservationEncoding.class, new ObservationEncodingKey(rfkt));
    }

    @Override
    public Set<ResponseFormatKey> getResponseFormatKeys() {
        return asResponseFormatKeys(getKeys(ObservationEncoding.class));
    }

    private Set<ResponseFormatKey> asResponseFormatKeys(
            List<ObservationEncodingKey> hkeys) {
        Set<ResponseFormatKey> keys = new HashSet<>(hkeys.size());
        for (ObservationEncodingKey key : hkeys) {
            keys.add(new ResponseFormatKey(new ServiceOperatorKey(key
                    .getService(), key.getVersion()), key.getEncoding()));
        }
        return keys;
    }

    // PROCEDURE DESCRIPTION FORMAT
    @Override
    public void setProcedureDescriptionFormatStatus(
            ProcedureDescriptionFormatKey pdfkt,
            boolean active) {
        setActive(ProcedureEncoding.class, new ProcedureEncoding(pdfkt), active);
    }

    @Override
    public boolean isProcedureDescriptionFormatActive(
            ProcedureDescriptionFormatKey pdfkt) {
        return isActive(ProcedureEncoding.class, new ProcedureEncodingKey(pdfkt));
    }

    @Override
    public Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys() {
        return asProcedureDescriptionFormatKeys(getKeys(ProcedureEncoding.class));
    }

    private Set<ProcedureDescriptionFormatKey> asProcedureDescriptionFormatKeys(
            List<ProcedureEncodingKey> hkeys) {
        Set<ProcedureDescriptionFormatKey> keys = new HashSet<>(hkeys.size());
        for (ProcedureEncodingKey key : hkeys) {
            keys
                    .add(new ProcedureDescriptionFormatKey(new ServiceOperatorKey(key
                                            .getService(), key.getVersion()), key
                                                           .getEncoding()));
        }
        return keys;
    }

    // OFFERING EXTENSION
    @Override
    public void setOfferingExtensionStatus(OfferingExtensionKey oek,
                                           boolean active) {
        setActive(DynamicOfferingExtension.class, new DynamicOfferingExtension(oek), active);
    }

    @Override
    public boolean isOfferingExtensionActive(OfferingExtensionKey oek) {
        return isActive(DynamicOfferingExtension.class, new DynamicOfferingExtensionKey(oek));
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeys() {
        return asOfferingExtensionKeys(getKeys(DynamicOfferingExtension.class));
    }

    private Set<OfferingExtensionKey> asOfferingExtensionKeys(
            List<DynamicOfferingExtensionKey> hkeys) {
        Set<OfferingExtensionKey> keys = new HashSet<>(hkeys.size());
        for (DynamicOfferingExtensionKey key : hkeys) {
            keys.add(new OfferingExtensionKey(new ServiceOperatorKey(key
                    .getService(), key.getVersion()), key.getDomain()));
        }
        return keys;
    }

}
