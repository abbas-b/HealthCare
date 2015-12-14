package com.doubleteam.healthcare.model;

/**
 * Created by bsh on 12/14/2015.
 */
public class DeviceListItems {
      private String hDeviceName;
        private String hDeviceAddress;

        public DeviceListItems(){

        }

        public DeviceListItems(String _hDeviceName,String _hDeviceAddress){
            this.hDeviceName=_hDeviceName;
            this.hDeviceAddress=_hDeviceAddress;
        }

        public void sethDeviceName(String _hDeviceName){
            this.hDeviceName=_hDeviceName;
        }

        public void sethDeviceAddress(String _hDeviceAddress){
            this.hDeviceAddress=_hDeviceAddress;
        }
        public String gethDeviceName(){
            return this.hDeviceName;
        }
        public String gethDeviceAddress(){
            return this.hDeviceAddress;
        }
}
