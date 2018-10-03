package com.benehiko;

import com.company.acs.AcsApplication;
import com.company.acs.acs.acs.device.Device;
import com.company.acs.acs.acs.device.DeviceManager;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicle;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleManager;
import com.company.acs.acs.acs.image.ImageManager;
import com.company.acs.acs.acs.location.Location;
import com.company.acs.acs.acs.location.LocationManager;
import com.company.acs.acs.acs.moderator.ModeratorManager;
import com.company.acs.acs.acs.numberplate.NumberplateManager;
import com.company.acs.acs.acs.user.User;
import com.company.acs.acs.acs.user.UserManager;
import com.company.acs.acs.acs.userauth.UserAuthManager;
import com.company.acs.acs.acs.userauth2.UserAuth2Manager;
import com.company.acs.acs.acs.usergroup.UserGroupManager;
import com.speedment.common.tuple.Tuple3;
import com.speedment.common.tuple.Tuples;
import com.speedment.runtime.core.exception.SpeedmentException;
import com.speedment.runtime.join.Join;
import com.speedment.runtime.join.JoinComponent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/db")
public class DBController {

    private final DeviceManager deviceManager;
    private final FleetVehicleManager fleetVehicleManager;
    private final ImageManager imageManager;
    private final LocationManager locationManager;
    private final ModeratorManager moderatorManager;
    private final NumberplateManager numberplateManager;
    private final UserManager userManager;
    private final UserAuthManager userAuthManager;
    private final UserAuth2Manager userAuth2Manager;
    private final UserGroupManager userGroupManager;
    private final JoinComponent joinComponent;

    public DBController(AcsApplication app) {
        deviceManager = app.getOrThrow(DeviceManager.class);
        fleetVehicleManager = app.getOrThrow(FleetVehicleManager.class);
        imageManager = app.getOrThrow(ImageManager.class);
        locationManager = app.getOrThrow(LocationManager.class);
        moderatorManager = app.getOrThrow(ModeratorManager.class);
        numberplateManager = app.getOrThrow(NumberplateManager.class);
        userManager = app.getOrThrow(UserManager.class);
        userAuthManager = app.getOrThrow(UserAuthManager.class);
        userAuth2Manager = app.getOrThrow(UserAuth2Manager.class);
        userGroupManager = app.getOrThrow(UserGroupManager.class);
        joinComponent = app.getOrThrow(JoinComponent.class);
    }

    @PutMapping("devices/{id}/addUser")
    boolean addDeviceUser(@RequestParam int id, @RequestBody User user){
        User tmpUser = userManager.stream().filter(User.USER_NAME.equal(user.getUserName())).findAny().orElse(null);
        Device tmpDevice = deviceManager.stream().filter(Device.DEVICE_ID.equal(id)).findAny().orElse(null);
        if (tmpDevice != null && tmpUser !=null){
            tmpDevice.setDeviceUser(tmpUser.getUserId());
            deviceManager.update(tmpDevice);
            return true;
        }
        return false;
    }

    @PostMapping("devices")
    boolean addDevice(@RequestBody Device device) {
        try {
            deviceManager.persist(device);
        } catch (SpeedmentException e) {
            return false;
        }
        return true;
    }

    @GetMapping("devices/{alias}")
    Device getDeviceByAlias(@PathVariable String alias) {
        return deviceManager.stream().filter(Device.ALIAS.equal(alias)).findAny().orElse(null);
    }

    @GetMapping("devices")
    List getDevices() {
        List devices = new ArrayList<>();
        Join<Tuple3<Device, User, Location>> join = joinComponent.from(DeviceManager.IDENTIFIER).innerJoinOn(User.USER_ID).equal(Device.DEVICE_USER).innerJoinOn(Location.LOCATION_ID).equal(Device.DEVICE_LOCATION).build(Tuples::of);
        join.stream().forEach(devices::add);
        return devices;
    }

    @GetMapping("fleetvehicles/{numberplate}")
    FleetVehicle getFleetByNumberplate(@PathVariable String numberplate) {
        return fleetVehicleManager.stream().filter(FleetVehicle.NUMBERPLATE.equal(numberplate)).findAny().orElse(null);
    }

    @GetMapping("fleetvehicles")
    List<FleetVehicle> getFleet() {
        return fleetVehicleManager.stream().collect(toList());
    }


}
