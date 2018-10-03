package com.company.acs.acs.acs.image.generated;

import com.company.acs.acs.acs.image.Image;
import com.company.acs.acs.acs.image.ImageManager;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.manager.AbstractManager;
import com.speedment.runtime.field.Field;

import java.util.stream.Stream;

/**
 * The generated base implementation for the manager of every {@link
 * com.company.acs.acs.acs.image.Image} entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedImageManagerImpl 
extends AbstractManager<Image> 
implements GeneratedImageManager {
    
    private final TableIdentifier<Image> tableIdentifier;
    
    protected GeneratedImageManagerImpl() {
        this.tableIdentifier = TableIdentifier.of("ACS", "ACS", "Image");
    }
    
    @Override
    public TableIdentifier<Image> getTableIdentifier() {
        return tableIdentifier;
    }
    
    @Override
    public Stream<Field<Image>> fields() {
        return ImageManager.FIELDS.stream();
    }
    
    @Override
    public Stream<Field<Image>> primaryKeyFields() {
        return Stream.of(
            Image.IMAGE_ID
        );
    }
}