package com.company.acs.acs.acs.location.generated;

import com.company.acs.acs.acs.location.Location;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.field.IntField;
import com.speedment.runtime.field.StringField;
import com.speedment.runtime.typemapper.TypeMapper;

/**
 * The generated base for the {@link
 * com.company.acs.acs.acs.location.Location}-interface representing entities of
 * the {@code Location}-table in the database.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedLocation {
    
    /**
     * This Field corresponds to the {@link Location} field that can be obtained
     * using the {@link Location#getLocationId()} method.
     */
    IntField<Location, Integer> LOCATION_ID = IntField.create(
        Identifier.LOCATION_ID,
        Location::getLocationId,
        Location::setLocationId,
        TypeMapper.primitive(),
        true
    );
    /**
     * This Field corresponds to the {@link Location} field that can be obtained
     * using the {@link Location#getName()} method.
     */
    StringField<Location, String> NAME = StringField.create(
        Identifier.NAME,
        Location::getName,
        Location::setName,
        TypeMapper.identity(),
        false
    );
    /**
     * This Field corresponds to the {@link Location} field that can be obtained
     * using the {@link Location#getType()} method.
     */
    StringField<Location, String> TYPE = StringField.create(
        Identifier.TYPE,
        Location::getType,
        Location::setType,
        TypeMapper.identity(),
        false
    );
    
    /**
     * Returns the locationId of this Location. The locationId field corresponds
     * to the database column ACS.ACS.Location.locationId.
     * 
     * @return the locationId of this Location
     */
    int getLocationId();
    
    /**
     * Returns the name of this Location. The name field corresponds to the
     * database column ACS.ACS.Location.name.
     * 
     * @return the name of this Location
     */
    String getName();
    
    /**
     * Returns the type of this Location. The type field corresponds to the
     * database column ACS.ACS.Location.type.
     * 
     * @return the type of this Location
     */
    String getType();
    
    /**
     * Sets the locationId of this Location. The locationId field corresponds to
     * the database column ACS.ACS.Location.locationId.
     * 
     * @param locationId to set of this Location
     * @return           this Location instance
     */
    Location setLocationId(int locationId);
    
    /**
     * Sets the name of this Location. The name field corresponds to the
     * database column ACS.ACS.Location.name.
     * 
     * @param name to set of this Location
     * @return     this Location instance
     */
    Location setName(String name);
    
    /**
     * Sets the type of this Location. The type field corresponds to the
     * database column ACS.ACS.Location.type.
     * 
     * @param type to set of this Location
     * @return     this Location instance
     */
    Location setType(String type);
    
    enum Identifier implements ColumnIdentifier<Location> {
        
        LOCATION_ID ("locationId"),
        NAME        ("name"),
        TYPE        ("type");
        
        private final String columnId;
        private final TableIdentifier<Location> tableIdentifier;
        
        Identifier(String columnId) {
            this.columnId        = columnId;
            this.tableIdentifier = TableIdentifier.of(    getDbmsId(), 
                getSchemaId(), 
                getTableId());
        }
        
        @Override
        public String getDbmsId() {
            return "ACS";
        }
        
        @Override
        public String getSchemaId() {
            return "ACS";
        }
        
        @Override
        public String getTableId() {
            return "Location";
        }
        
        @Override
        public String getColumnId() {
            return this.columnId;
        }
        
        @Override
        public TableIdentifier<Location> asTableIdentifier() {
            return this.tableIdentifier;
        }
    }
}