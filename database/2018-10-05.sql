create table Location
(
  locationId int auto_increment
    primary key,
  name       varchar(45) not null,
  type       varchar(45) not null
);

create table UserGroup
(
  usergroupId int auto_increment,
  name        varchar(32)     not null,
  level       int default '1' not null,
  constraint UserGroup_name_uindex
  unique (name),
  constraint UserGroup_usergroupId_uindex
  unique (usergroupId)
);

alter table UserGroup
  add primary key (usergroupId);

create table User
(
  userId           int auto_increment,
  firstName        varchar(45) not null,
  lastName         varchar(45) not null,
  username         varchar(45) not null,
  user_usergroupId int         not null,
  constraint User_Id_UNIQUE
  unique (userId),
  constraint userName_UNIQUE
  unique (username),
  constraint Users_UserGroup_usergroupId_fk
  foreign key (user_usergroupId) references UserGroup (usergroupId)
);

alter table User
  add primary key (userId);

create table Device
(
  deviceId        int auto_increment,
  mac             varchar(45) not null,
  alias           varchar(45) not null,
  device_location int         null,
  device_user     int         null,
  constraint Device_Id_UNIQUE
  unique (deviceId),
  constraint Devices_deviceMac_uindex
  unique (mac),
  constraint Device_Location_locationId_fk
  foreign key (device_location) references Location (locationId)
    on update set null
    on delete set null,
  constraint Device_User_userId_fk
  foreign key (device_user) references User (userId)
    on update set null
    on delete set null
);

alter table Device
  add primary key (deviceId);

create table FleetVehicle
(
  vehicleId   int         not null,
  numberplate varchar(45) not null,
  fleet_user  int         not null,
  constraint FleetVehicle_numberplate_uindex
  unique (numberplate),
  constraint vehicleId_UNIQUE
  unique (vehicleId),
  constraint FleetVehicle_User_userId_fk
  foreign key (fleet_user) references User (userId)
    on update cascade
    on delete cascade
);

alter table FleetVehicle
  add primary key (vehicleId);

create table Image
(
  imageId      int auto_increment,
  image        longblob                            not null,
  timeStamp    timestamp default CURRENT_TIMESTAMP not null
  on update CURRENT_TIMESTAMP,
  image_device int                                 not null,
  constraint imageId_UNIQUE
  unique (imageId),
  constraint Image_Device_deviceId_fk
  foreign key (image_device) references Device (deviceId)
);

alter table Image
  add primary key (imageId);

create table Numberplate
(
  numberplateId     int auto_increment
    primary key,
  numberplatestring varchar(45)                         not null,
  time              timestamp default CURRENT_TIMESTAMP not null
  on update CURRENT_TIMESTAMP,
  numberplate_image int                                 not null,
  constraint Numberplate_Image_imageId_fk
  foreign key (numberplate_image) references Image (imageId)
    on update cascade
    on delete cascade
);

create table UserAuth
(
  authId   int auto_increment,
  username varchar(64)  not null,
  hash     varchar(256) not null,
  salt     varchar(256) not null,
  constraint UserAuth_authId_uindex
  unique (authId),
  constraint UserAuth_username_uindex
  unique (username),
  constraint UserAuth_User_userName_fk
  foreign key (username) references User (username)
    on update cascade
    on delete cascade
);

alter table UserAuth
  add primary key (authId);

create table UserAuth2
(
  authId      int auto_increment,
  auth_userId int          not null,
  hash        varchar(256) not null,
  salt        varchar(256) not null,
  constraint UserAuth2_authId_uindex
  unique (authId),
  constraint UserAuth2_User_userId_fk
  foreign key (auth_userId) references User (userId)
    on update cascade
    on delete cascade
);

alter table UserAuth2
  add primary key (authId);


