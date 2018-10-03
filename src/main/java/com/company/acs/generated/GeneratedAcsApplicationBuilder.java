package com.company.acs.generated;

import com.company.acs.AcsApplication;
import com.company.acs.AcsApplicationBuilder;
import com.company.acs.AcsApplicationImpl;
import com.company.acs.acs.acs.device.DeviceManagerImpl;
import com.company.acs.acs.acs.device.DeviceSqlAdapter;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleManagerImpl;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleSqlAdapter;
import com.company.acs.acs.acs.image.ImageManagerImpl;
import com.company.acs.acs.acs.image.ImageSqlAdapter;
import com.company.acs.acs.acs.location.LocationManagerImpl;
import com.company.acs.acs.acs.location.LocationSqlAdapter;
import com.company.acs.acs.acs.numberplate.NumberplateManagerImpl;
import com.company.acs.acs.acs.numberplate.NumberplateSqlAdapter;
import com.company.acs.acs.acs.user.UserManagerImpl;
import com.company.acs.acs.acs.user.UserSqlAdapter;
import com.company.acs.acs.acs.userauth.UserAuthManagerImpl;
import com.company.acs.acs.acs.userauth.UserAuthSqlAdapter;
import com.company.acs.acs.acs.userauth2.UserAuth2ManagerImpl;
import com.company.acs.acs.acs.userauth2.UserAuth2SqlAdapter;
import com.company.acs.acs.acs.usergroup.UserGroupManagerImpl;
import com.company.acs.acs.acs.usergroup.UserGroupSqlAdapter;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.common.injector.Injector;
import com.speedment.runtime.application.AbstractApplicationBuilder;

/**
 * A generated base {@link
 * com.speedment.runtime.application.AbstractApplicationBuilder} class for the
 * {@link com.speedment.runtime.config.Project} named ACS.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedAcsApplicationBuilder extends AbstractApplicationBuilder<AcsApplication, AcsApplicationBuilder> {
    
    protected GeneratedAcsApplicationBuilder() {
        super(AcsApplicationImpl.class, GeneratedAcsMetadata.class);
        withManager(DeviceManagerImpl.class);
        withManager(FleetVehicleManagerImpl.class);
        withManager(ImageManagerImpl.class);
        withManager(LocationManagerImpl.class);
        withManager(NumberplateManagerImpl.class);
        withManager(UserManagerImpl.class);
        withManager(UserAuthManagerImpl.class);
        withManager(UserAuth2ManagerImpl.class);
        withManager(UserGroupManagerImpl.class);
        withComponent(DeviceSqlAdapter.class);
        withComponent(FleetVehicleSqlAdapter.class);
        withComponent(ImageSqlAdapter.class);
        withComponent(LocationSqlAdapter.class);
        withComponent(NumberplateSqlAdapter.class);
        withComponent(UserSqlAdapter.class);
        withComponent(UserAuthSqlAdapter.class);
        withComponent(UserAuth2SqlAdapter.class);
        withComponent(UserGroupSqlAdapter.class);
    }
    
    @Override
    public AcsApplication build(Injector injector) {
        return injector.getOrThrow(AcsApplication.class);
    }
}