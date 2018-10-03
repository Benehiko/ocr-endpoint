package com.benehiko;

import com.company.acs.AcsApplication;
import com.company.acs.acs.acs.device.Device;
import com.company.acs.acs.acs.device.DeviceImpl;
import com.company.acs.acs.acs.device.DeviceManager;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicle;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleImpl;
import com.company.acs.acs.acs.fleetvehicle.FleetVehicleManager;
import com.company.acs.acs.acs.image.Image;
import com.company.acs.acs.acs.image.ImageManager;
import com.company.acs.acs.acs.location.Location;
import com.company.acs.acs.acs.location.LocationManager;
import com.company.acs.acs.acs.numberplate.Numberplate;
import com.company.acs.acs.acs.numberplate.NumberplateManager;
import com.company.acs.acs.acs.user.User;
import com.company.acs.acs.acs.user.UserImpl;
import com.company.acs.acs.acs.user.UserManager;
import com.company.acs.acs.acs.userauth.UserAuthManager;
import com.company.acs.acs.acs.userauth2.UserAuth2Manager;
import com.company.acs.acs.acs.usergroup.UserGroup;
import com.company.acs.acs.acs.usergroup.UserGroupImpl;
import com.company.acs.acs.acs.usergroup.UserGroupManager;
import com.speedment.common.tuple.Tuple3;
import com.speedment.common.tuple.Tuples;
import com.speedment.runtime.core.exception.SpeedmentException;
import com.speedment.runtime.join.Join;
import com.speedment.runtime.join.JoinComponent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/db")
public class DBController {

    private final DeviceManager deviceManager;
    private final FleetVehicleManager fleetVehicleManager;
    private final ImageManager imageManager;
    private final LocationManager locationManager;
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
        numberplateManager = app.getOrThrow(NumberplateManager.class);
        userManager = app.getOrThrow(UserManager.class);
        userAuthManager = app.getOrThrow(UserAuthManager.class);
        userAuth2Manager = app.getOrThrow(UserAuth2Manager.class);
        userGroupManager = app.getOrThrow(UserGroupManager.class);
        joinComponent = app.getOrThrow(JoinComponent.class);
    }

    /*
    /------------------------/
    Devices Part
    /-----------------------/
     */

    /**
     * @param id
     * @param username
     * @return
     */
    @PutMapping(path = "devices/addUser/byId", produces = "application/json")
    @ResponseBody
    boolean addDeviceUserById(@RequestParam("id") int id, @RequestParam("username") String username) {
        try {
            Optional<User> user = userManager.stream().filter(User.USERNAME.equalIgnoreCase(username)).findFirst();
            Optional<Device> device = deviceManager.stream().filter(Device.DEVICE_ID.equal(id)).findFirst();
            if (user.isPresent() && device.isPresent()) {
                device.get().setDeviceUser(user.get().getUserId());
                deviceManager.update(device.get());
                return true;

            }
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @PutMapping("devices/addUser/byMac")
    @ResponseBody
    boolean addDeviceUserByMac(@RequestParam("mac") String mac, @RequestParam("username") String username) {
        try{
            Optional<User> user = userManager.stream().filter(User.USERNAME.equalIgnoreCase(username)).findFirst();
            Optional<Device> device = deviceManager.stream().filter(Device.MAC.equalIgnoreCase(mac)).findFirst();
            if (user.isPresent() && device.isPresent()){
                device.get().setDeviceUser(user.get().getUserId());
                deviceManager.update(device.get());
                return true;
            }
        }catch (SpeedmentException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @PutMapping("devices/addLocation/byMac")
    @ResponseBody
    boolean addDeviceLocationByMac(@RequestParam("mac") String mac, @RequestParam("location") int locationId){
        try{
            Optional<Location> location = locationManager.stream().filter(Location.LOCATION_ID.equal(locationId)).findFirst();
            Optional<Device> device = deviceManager.stream().filter(Device.MAC.equalIgnoreCase(mac)).findFirst();

            if (device.isPresent() && location.isPresent()){
                device.get().setDeviceLocation(location.get().getLocationId());
                deviceManager.update(device.get());
                return true;
            }
        } catch (SpeedmentException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @PostMapping("devices")
    @ResponseBody
    boolean addDevice(@RequestParam("mac") String mac, @RequestParam("alias") String alias) {
        try {
            Device device = new DeviceImpl().setMac(mac).setAlias(alias);
            deviceManager.persist(device);
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @GetMapping("devices/{alias}")
    @ResponseBody
    Optional<Device> getDeviceByAlias(@PathVariable String alias) {
        return deviceManager.stream().filter(Device.ALIAS.equalIgnoreCase(alias)).findAny();
    }

    @GetMapping("devices/{mac}")
    @ResponseBody
    Optional<Device> getDeviceByMac(@PathVariable String mac) {
        return deviceManager.stream().filter(Device.MAC.equalIgnoreCase(mac)).findAny();
    }

    @GetMapping("devices")
    @ResponseBody
    List getDevices() {
        return deviceManager.stream().collect(toList());
    }

    /*
    /------------------------/
    Fleet Vehicles part
    /-----------------------/
     */

    /**
     * Return a specific fleet vehicle specified by the numberplate identifier
     *
     * @param numberplate
     * @return
     */
    @GetMapping("fleetvehicles/{numberplate}")
    FleetVehicle getFleetByNumberplate(@PathVariable String numberplate) {
        return fleetVehicleManager.stream().filter(FleetVehicle.NUMBERPLATE.equal(numberplate)).findAny().orElse(null);
    }

    /**
     * Return all fleet vehicles
     *
     * @return
     */
    @GetMapping("fleetvehicles")
    @ResponseBody
    List<FleetVehicle> getFleet() {
        return fleetVehicleManager.stream().collect(toList());
    }

    @PostMapping("fleetvehicles")
    @ResponseBody
    boolean addFleetVehicle(@RequestParam("numberplate") String numberplate, @RequestParam("username") String username) throws SpeedmentException {
        Optional<User> user = userManager.stream().filter(User.USERNAME.equalIgnoreCase(username)).findFirst();
        if (user.isPresent()) {
            FleetVehicle fleetVehicle = new FleetVehicleImpl().setNumberplate(numberplate).setFleetUser(user.get().getUserId());
            fleetVehicleManager.persist(fleetVehicle);
        }
        return true;
    }

    @DeleteMapping("fleetvehicles")
    @ResponseBody
    boolean removeFleetVehicle(@RequestParam("numberplate") String numberplate) throws SpeedmentException {
        Optional<FleetVehicle> fleetVehicle = fleetVehicleManager.stream().filter(FleetVehicle.NUMBERPLATE.equalIgnoreCase(numberplate)).findFirst();
        fleetVehicle.ifPresent(fleetVehicleManager::remove);
        return true;
    }


    /*
    /---------------------/
    User part
    /---------------------/
     */

    /**
     * Return all Users
     *
     * @return
     */
    @GetMapping("users")
    @ResponseBody
    List<User> getUsers() {
        return userManager.stream().collect(toList());
    }

    /**
     * Return specific User by USERID
     *
     * @param userId
     * @return
     */
    @GetMapping("users/{id}")
    @ResponseBody
    Optional<User> getUserById(@PathVariable int userId) {
        return userManager.stream().filter(User.USER_ID.equal(userId)).findFirst();
    }

    /**
     * Return all Users by Firstname and Lastname
     *
     * @param firstname
     * @param lastname
     * @return
     */
    @GetMapping("users/{firstname}/{lastname}")
    @ResponseBody
    List<User> getUserByName(@PathVariable String firstname, @PathVariable String lastname) {
        return userManager.stream().filter(User.FIRST_NAME.equalIgnoreCase(firstname).and(User.LAST_NAME.equalIgnoreCase(lastname))).collect(toList());
    }

    /**
     * Return User by Username
     *
     * @param username
     * @return
     */
    @GetMapping("users/{username}")
    @ResponseBody
    Optional<User> getUserByUsername(@PathVariable String username) {
        return userManager.stream().filter(User.USERNAME.equalIgnoreCase(username)).findFirst(); //collect(toList());
    }

    /**
     * Add User
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param groupName
     * @return
     */
    @PostMapping("users")
    @ResponseBody
    boolean addUser(@RequestParam("username") String username, @RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName, @RequestParam("usergroup") String groupName) {
        try {
            Optional<UserGroup> userGroup = userGroupManager.stream().filter(UserGroup.NAME.equal(groupName)).findFirst();
            int userGroupId;

            if (!userGroup.isPresent()) {
                UserGroup userGroup1 = new UserGroupImpl().setLevel(1).setName(groupName);
                userGroupManager.persist(userGroup1);
                userGroupId = userGroup1.getUsergroupId();
            } else {
                userGroupId = userGroup.get().getUsergroupId();
            }
            User user = new UserImpl().setUsername(username).setFirstName(firstName).setLastName(lastName).setUserUsergroupId(userGroupId);
            userManager.persist(user);
            return true;
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Delete user based on username
     *
     * @param username
     * @return
     */
    @DeleteMapping("users")
    @ResponseBody
    boolean removeUser(@RequestParam("username") String username) {
        try {
            Optional<User> user = userManager.stream().filter(User.USERNAME.equal(username)).findFirst();
            if (user.isPresent()) {
                userManager.remove(user.get());
                return true;
            }
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Update User
     *
     * @param username
     * @param firstname
     * @param lastname
     * @param usergroup
     * @return boolean based on if update succeeded or not
     */
    @PutMapping("users")
    @ResponseBody
    boolean updateUser(@RequestParam("username") String username, @RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("usergroup") String usergroup) {
        try {
            UserGroup userGroup = userGroupManager.stream().filter(UserGroup.NAME.equalIgnoreCase(usergroup)).findFirst().orElse(null);
            if (userGroup == null) {
                userGroup = new UserGroupImpl().setLevel(1).setName(usergroup);
                userGroupManager.persist(userGroup);
            }
            User user = new UserImpl().setUsername(username).setFirstName(firstname).setLastName(lastname).setUserUsergroupId(userGroup.getUsergroupId());
            userManager.update(user);
            return true;
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    /*
    /-------------------------/
    Vehicles recorded
    /-------------------------/
     */

    /**
     * Return all Vehicles recorded in the system
     *
     * @return
     */
    @GetMapping("vehicles")
    @ResponseBody
    List getVehicles() {
        List vehicles = new ArrayList<>();
        Join<Tuple3<Numberplate, Image, Device>> join = joinComponent.from(NumberplateManager.IDENTIFIER).innerJoinOn(Image.IMAGE_DEVICE).equal(Numberplate.NUMBERPLATE_ID).innerJoinOn(Device.DEVICE_ID).equal(Image.IMAGE_DEVICE).build(Tuples::of);
        join.stream().forEachOrdered(vehicles::add);
        return vehicles;
    }

    /**
     * Get Vehicles between from and to date
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping("vehicles/{from}/{to}")
    @ResponseBody
    List getVehiclesByDate(@PathVariable Timestamp from, @PathVariable Timestamp to) {
        List vehicles = new ArrayList<>();
        vehicles = numberplateManager.stream().filter(Numberplate.TIME.between(from, to)).mapToInt(Numberplate.NUMBERPLATE_ID).boxed().collect(toList());
        Join<Tuple3<Image, Numberplate, Device>> join = joinComponent.from(ImageManager.IDENTIFIER).innerJoinOn(Numberplate.NUMBERPLATE_IMAGE).equal(Numberplate.NUMBERPLATE_ID).where(Numberplate.TIME.between(from, to)).innerJoinOn(Device.DEVICE_ID).equal(Image.IMAGE_DEVICE).build(Tuples::of);
        List result = new ArrayList<>();
        join.stream().forEachOrdered(result::add);
        return result;
    }


    /*
    /----------------------/
    Get fleet vehicles
    /---------------------/
     */

    /**
     * Get fleetVehicles
     *
     * @param fleetVehicle
     * @return
     */
    @PostMapping(path = "vehicles", consumes = "application/json", produces = "application/json")
    @ResponseBody
    boolean addVehicle(@RequestBody FleetVehicle fleetVehicle) {
        try {
            fleetVehicleManager.persist(fleetVehicle);
            return true;
        } catch (SpeedmentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


}
