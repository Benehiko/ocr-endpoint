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
import com.company.acs.acs.acs.image.generated.GeneratedImage;
import com.company.acs.acs.acs.location.Location;
import com.company.acs.acs.acs.location.LocationImpl;
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
import com.speedment.common.tuple.Tuple2;
import com.speedment.common.tuple.Tuples;
import com.speedment.runtime.core.exception.SpeedmentException;
import com.speedment.runtime.join.Join;
import com.speedment.runtime.join.JoinComponent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @GetMapping("devices/byAlias/{alias}")
    @ResponseBody
    Optional<Device> getDeviceByAlias(@PathVariable String alias) {
        return deviceManager.stream().filter(Device.ALIAS.equalIgnoreCase(alias)).findAny();
    }

    @GetMapping("devices/byMac/{mac}")
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
    @GetMapping("fleetvehicles/byNumberplate/{numberplate}")
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

    @GetMapping("fleetvehiclesWithUsers")
    @ResponseBody
    List getFleetWithUsers(){
        List result = new ArrayList<>();
        Join<Tuple2<FleetVehicle, User>> join = joinComponent.from(FleetVehicleManager.IDENTIFIER).innerJoinOn(User.USER_ID).equal(FleetVehicle.FLEET_USER).build(Tuples::of);
        join.stream().forEachOrdered(result::add);
        return result;
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
    @GetMapping("users/byId/{id}")
    @ResponseBody
    Optional<User> getUserById(@PathVariable int id) {
        return userManager.stream().filter(User.USER_ID.equal(id)).findFirst();
    }

    /**
     * Return all Users by Firstname and Lastname
     *
     * @param firstname
     * @param lastname
     * @return
     */
    @GetMapping("users/byName/{firstname}/{lastname}")
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
    @GetMapping("users/byUsername/{username}")
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
    List<Numberplate> getVehicles() {
        return numberplateManager.stream().collect(toList());
    }

    @GetMapping("vehicles/byNumberplate/{numberplate}")
    @ResponseBody
    List<Numberplate> getVehiclesByNumberplate(@PathVariable String numberplate){
        return numberplateManager.stream().filter(Numberplate.NUMBERPLATESTRING.equalIgnoreCase(numberplate)).collect(toList());
    }

    /**
     * Get Vehicles between from and to date
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping("vehicles/byDate/{from}/{to}")
    @ResponseBody
    List<Numberplate> getVehiclesByDate(@PathVariable String from, @PathVariable String to) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H");
        Date fromParsed = dateFormat.parse(from);
        Date toParsed = dateFormat.parse(to);
        Timestamp fromDate = new java.sql.Timestamp(fromParsed.getTime());
        Timestamp toDate = new java.sql.Timestamp(toParsed.getTime());

        return numberplateManager.stream().filter(Numberplate.TIME.between(fromDate, toDate)).collect(toList());

    }

    @GetMapping("vehicles/byDateNumberplate/{from}/{to}/{numberplate}")
    @ResponseBody
    List<Numberplate> getVehiclesByDate(@PathVariable String from, @PathVariable String to, @PathVariable String numberplate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H");
        Date fromParsed = dateFormat.parse(from);
        Date toParsed = dateFormat.parse(to);
        Timestamp fromDate = new java.sql.Timestamp(fromParsed.getTime());
        Timestamp toDate = new java.sql.Timestamp(toParsed.getTime());

        return numberplateManager.stream().filter(Numberplate.TIME.between(fromDate, toDate)).filter(Numberplate.NUMBERPLATESTRING.equalIgnoreCase(numberplate)).collect(toList());
    }

    @GetMapping("vehicles/byDevice/{alias}")
    @ResponseBody
    Map<Integer, List<Numberplate>> getVehiclesByDevice(@PathVariable String alias){
        List<Device> devices = deviceManager.stream().filter(Device.ALIAS.equalIgnoreCase(alias)).collect(toList());
        Map<Integer, List<Numberplate>> resultset = new HashMap<>();
        devices.forEach(i -> {
            List<Integer> images = imageManager.stream().filter(Image.IMAGE_DEVICE.equal(i.getDeviceId())).mapToInt(GeneratedImage::getImageId).boxed().collect(toList());
            images.forEach(imageId -> {
                List<Numberplate> numberplate = numberplateManager.stream().filter(Numberplate.NUMBERPLATE_IMAGE.equal(imageId)).collect(toList());
                resultset.put(i.getDeviceId(), numberplate);
            });

        });
        return resultset;
    }

    @GetMapping("vehicles/image/byVehicleId/{id}")
    @ResponseBody
    String getVehicleImage(@PathVariable int id) throws SQLException {
        Optional<Numberplate> numberplate = numberplateManager.stream().filter(Numberplate.NUMBERPLATE_ID.equal(id)).findFirst();
        String out = null;
        if (numberplate.isPresent()) {
            Optional<Blob> blob = imageManager.stream().filter(Image.IMAGE_ID.equal(numberplate.get().getNumberplateImage())).map(Image::getImage).findFirst();
            if (blob.isPresent()){
                try {
                    InputStream is = blob.get().getBinaryStream();
                    byte[] b = is.readAllBytes();
                    out = Base64.getEncoder().encodeToString(b);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return out;
    }



    /*
    /-----------------/
    Location
    /-----------------/
     */
    @GetMapping("locations")
    @ResponseBody
    List getLocations(){
        return locationManager.stream().collect(toList());
    }

    @GetMapping("locations/{id}")
    Optional<Location> getLocationById(@PathVariable int id){
        return locationManager.stream().filter(Location.LOCATION_ID.equal(id)).findFirst();
    }

    @GetMapping("locations/{name}")
    List<Location> getLocationsByName(@PathVariable String name){
        return locationManager.stream().filter(Location.NAME.equalIgnoreCase(name)).collect(toList());
    }

    @PostMapping("locations")
    @ResponseBody
    boolean addLocation(@RequestParam("name") String name, @RequestParam("type") String type){
        try {
            Location location = new LocationImpl().setName(name).setType(type);
            locationManager.persist(location);
            return true;
        }catch (SpeedmentException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @PutMapping("locations")
    @ResponseBody
    boolean updateLocation(@RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("type") String type){
        try{
            Optional<Location> location = locationManager.stream().filter(Location.LOCATION_ID.equal(id)).findFirst();

            if (location.isPresent()){
                Location tmp = location.get().setName(name).setType(type);
                locationManager.update(tmp);
                return true;
            }
        }catch (SpeedmentException e){
            System.out.println(e.getMessage());
        }
        return true;
    }


    /*
    /--------------------/
    UserGroup
    /------------------/
     */

    @GetMapping("usergroups")
    @ResponseBody
    List<UserGroup> getUserGroups(){
        return userGroupManager.stream().collect(toList());
    }

    @PostMapping("usergroups")
    @ResponseBody
    boolean addUserGroup(@RequestParam("name") String name, @RequestParam("level") int level){
        try {
            UserGroup userGroup = new UserGroupImpl().setName(name).setLevel(level);
            userGroupManager.persist(userGroup);
        }catch (SpeedmentException e){
            System.out.println(e.getMessage());
        }
        return false;
    }


}
