package com.cdran.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cdran.model.entity.Farm;
import com.cdran.model.entity.Farmer;
import com.cdran.repository.CropRepository;
import com.cdran.repository.UserRepository;
import com.cdran.service.FarmService;

@Controller
@RequestMapping("/farmer/farms")
public class FarmController {

    private final FarmService farmService;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    public FarmController(FarmService farmService, CropRepository cropRepository, UserRepository userRepository) {
        this.farmService = farmService;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listFarms(Model model, @AuthenticationPrincipal(expression = "username") String phone) {
        Farmer farmer = requireFarmer(phone);
        model.addAttribute("farms", farmService.getFarmsByFarmer(farmer));
        return "farmer/farm-list";
    }

    @GetMapping("/new")
    public String showAddFarmForm(Model model, @AuthenticationPrincipal(expression = "username") String phone) {
        requireFarmer(phone);
        model.addAttribute("farm", new Farm());
        model.addAttribute("allCrops", cropRepository.findAll());
        return "farmer/farm-form";
    }

    @PostMapping("/save")
    public String saveFarm(@ModelAttribute Farm farm, @AuthenticationPrincipal(expression = "username") String phone) {
        farmService.saveFarm(farm, requireFarmer(phone));
        return "redirect:/farmer/farms";
    }

    private Farmer requireFarmer(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new UsernameNotFoundException("Authenticated farmer account not found");
        }

        return userRepository.findByPhone(phone.trim())
                .filter(Farmer.class::isInstance)
                .map(Farmer.class::cast)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated farmer account not found"));
    }
}