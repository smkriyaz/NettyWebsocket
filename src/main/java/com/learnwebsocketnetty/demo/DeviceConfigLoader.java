package com.learnwebsocketnetty.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceConfigLoader {

    private List<DeviceConfig> deviceConfigList;

    @PostConstruct
    public void loadconfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Config.json");
        deviceConfigList = mapper.readValue(inputStream, new TypeReference<List<DeviceConfig>>() {});

    }

    public List<DeviceConfig> getDeviceConfigs() {
        return deviceConfigList;
    }
}
