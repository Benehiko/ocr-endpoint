package com.company.acs.acs.acs.fleetvehicle.generated;

import com.company.acs.acs.acs.fleetvehicle.FleetVehicle;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleManager;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.manager.AbstractManager;
import com.speedment.runtime.field.Field;

import java.util.stream.Stream;

/**
 * The generated base implementation for the manager of every {@link
 * com.company.acs.acs.acs.fleetvehicle.FleetVehicle} entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedFleetVehicleManagerImpl 
extends AbstractManager<FleetVehicle> 
implements GeneratedFleetVehicleManager {
    
    private final TableIdentifier<FleetVehicle> tableIdentifier;
    
    protected GeneratedFleetVehicleManagerImpl() {
        this.tableIdentifier = TableIdentifier.of("ACS", "ACS", "FleetVehicle");
    }
    
    @Override
    public TableIdentifier<FleetVehicle> getTableIdentifier() {
        return tableIdentifier;
    }
    
    @Override
    public Stream<Field<FleetVehicle>> fields() {
        return FleetVehicleManager.FIELDS.stream();
    }
    
    @Override
    public Stream<Field<FleetVehicle>> primaryKeyFields() {
        return Stream.of(
            FleetVehicle.VEHICLE_ID
        );
    }
}