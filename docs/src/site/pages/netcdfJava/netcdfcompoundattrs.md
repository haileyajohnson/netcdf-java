---
title: NetCDF Compound Attributes
last_updated: 2019-07-17
sidebar: netcdfJavaTutorial_sidebar 
permalink: netcdf_compound_attrs.html
toc: false
---

## NetCDF-4 Compound Attributes

last changed 07/15/2019

NetCDF4 / HDF5 does not support attributes on fields in Compound types. These are proposed Conventions to support attributes on individual fields in a Compound type.

Consider an example of a compound variable in NetCDF-4, using CDL:

~~~
netcdf point.nc4 {
 types:
  compound record_t {
    double time ;
    double latitude ;
    double longitude ;
    byte data ;
    float z ;
  }
 
 dimensions:
   obs = UNLIMITED ; // (5 currently)
 
 variables:
   record_t record(obs) ;
}
~~~

We would like to add any number of attributes of arbitrary type to the fields of this compound type. For example (in CDM), suppose we want to add the following attributes:

~~~
netcdf point.nc {
  dimensions:
    time= 5;

  variables:
   Structure {
    int lon;
      :long_name = "longitude";
      :units = "degrees_east";
    int lat;
      :long_name = "latitude";
      :units = "degrees_north";
    float z;
      :long_name = "height above mean sea level";
      :units = "km";
      :positive = "up";
    short time;
      :units = "days since 1970-01-01 00:00:00";
    byte data;
      :long_name = "skin temperature";
      :units = "Celsius";
      :coordinates = "time lon lat z";
      :calibration = 1382.89f, 12.0f, 0.008f; // float
   } obs(time);

}
~~~

### Arbitrary field attributes Proposal

The "Arbitrary field attributes" proposal is to add a compound attribute named _"_field_atts"_ to the compound variable, with one field for each attribute on any of the fields. Example in CDL:

~~~
netcdf point.nc4 {
 types:
  compound record_t {
    double time ;
    double latitude ;
    double longitude ;
    byte data ;
    float z ;
  }; 
  compound _record_field_atts_t {
    string time\:units;
    string latitude\:units;
    string latitude\:long_name ;
    string longitude\:units;
    string longitude\:long_name;
    string data\:units;
    string data\:long_name;
    string data\:coordinates;
    float data\:calibration(3) ;
    string z\:units;
    string z\:long_name;
    string z\:positive;
  }; 

dimensions:
  obs = UNLIMITED ; // (5 currently)

variables:
  record_t record(obs) ;
    _record_field_atts_t record:_field_atts =

     {"degrees_north"}, {"station latitude"}, 
     {"degrees_east"}, {"station longitude"}, 
     {"Celsius"}, {"skin temperature"}, {"time lon lat z"}, { 1382.89f, 12.0f, 0.008f}
     {"km"}, {"height above mean sea level"}, {"up"}} ;
     
~~~
     
Notes:

1. The compound attribute type must be named "_<compound name>_field_atts_t", where <compound name> is the name of the variable's compound type. This prevents possible conflicts with other compound attributes.
2. The compound attribute must be named "_field_atts".
3. The fields of the compound atttribute must be named "<field name>:<attribute_name>", using the colon ":" as a delimiter. The CDL escapes the ":", but the backslash is not part of the name.
4. Only one compound attribute per compound variable can be added, which contains any number of attributes for the individual fields.

### Natural field attributes proposal

An important use case is to add missing values to a compound type. In this case, there is no need to add a separate compound type, and in practice would be confusing. Example:

~~~
types:
  compound wind_vector_t {
    float eastward ;
    float northward ;
  }
dimensions:
    station = 53434 ;
variables:
    wind_vector_t wind(station) ;
       wind_vector_t wind:_FillValue = {-9999, -9999} ;
~~~

### Notes:

1. The type of the compound attribute must be the same as the type of the variable.
2. The name of the compound attribute is the name of the attribute to be distributed to the members.
3. Any number of attributes for a compound variable can be added using this convention. The type of the attribute for a field is always the same as the field itself.

### Summary

We propose to allow adding attributes to fields in a compound type, using both the <b>_"Arbitrary field attributes"_</b> and the <b>_"Natural field attributes"_</b> conventions. Both require no changes to the existing NetCDF / HDF file format or APIs. Both are conventions on how libraries and applications should understand certain compound attributes.

The NetCDF Java library will follow these conventions and hide the implementation details from the user. The NetCDF C library might in the future do the same.

