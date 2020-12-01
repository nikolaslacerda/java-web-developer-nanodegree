package com.udacity.pricing.api;

import com.udacity.pricing.service.PriceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PricingController.class)
public class PricingControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetPrices() throws Exception {
        long vehicleId = 1L;
        mockMvc.perform(get("/services/price?vehicleId=" + vehicleId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.vehicleId").value(1));
    }

    @Test
    public void shouldReturnNotFoundWhenVehicleIdIsInvalid() throws Exception {
        long vehicleId = 21L;
        mockMvc.perform(get("/services/price?vehicleId=" + vehicleId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

}

