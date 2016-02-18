/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation.series.full;

import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.observation.GeologyLogCoverageGeneratorSplitter;
import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.VoidObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.VoidValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.GeologyLogCoverageObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ogc.om.values.GWGeologyLogCoverage;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class SeriesGeologyLogCoverageObservation extends AbstractSeriesObservation<GWGeologyLogCoverage>
    implements GeologyLogCoverageObservation {

    private static final long serialVersionUID = -3825062615148354938L;
    private GWGeologyLogCoverage value;
    private Double fromDepth;
    private Double toDepth;
    private Unit depthunit;
    private Double logValue;

    @Override
    public GWGeologyLogCoverage getValue() {
        if (value == null) {
            setValue(GeologyLogCoverageGeneratorSplitter.create(this));
        }
        return value;
    }

    @Override
    public void setValue(GWGeologyLogCoverage value) {
       this.value = value;
       GeologyLogCoverageGeneratorSplitter.split(value, this);
    }

    @Override
    public boolean isSetValue() {
        return getValue() != null;
    }

    @Override
    public String getValueAsString() {
        return getLogValue().toString();
    }

    @Override
    public Double getFromDepth() {
        return fromDepth;
    }

    @Override
    public void setFromDepth(Double fromDepth) {
        this.fromDepth = fromDepth;
    }

    @Override
    public boolean isSetFromDepth() {
        return getFromDepth() != null;
    }

    @Override
    public Double getToDepth() {
        return toDepth;
    }

    @Override
    public void setToDepth(Double toDepth) {
        this.toDepth = toDepth;
    }

    @Override
    public boolean isSetToDepth() {
        return getToDepth() != null;
    }

    @Override
    public Unit getDepthunit() {
        return depthunit;
    }

    @Override
    public void setDepthunit(Unit depthunit) {
        this.depthunit = depthunit;
    }

    @Override
    public boolean isSetDephtUnit() {
        return getDepthunit() != null && getDepthunit().isSetUnit();
    }

    @Override
    public Double getLogValue() {
        return logValue;
    }

    @Override
    public void setLogValue(Double logValue) {
        this.logValue = logValue;
    }

    @Override
    public boolean isSetLogValue() {
        return getLogValue() != null;
    }

    @Override
    public void accept(VoidObservationVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ObservationVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValuedObservationVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ValuedObservationVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }
}