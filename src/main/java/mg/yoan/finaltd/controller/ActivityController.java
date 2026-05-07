package mg.yoan.finaltd.controller;

import lombok.RequiredArgsConstructor;
import mg.yoan.finaltd.entity.Activity;
import mg.yoan.finaltd.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public List<Activity> addActivities(@PathVariable String id, @RequestBody List<Activity> activities) {
        return activityService.addActivities(id, activities);
    }

    @GetMapping
    public List<Activity> getActivities(@PathVariable String id) {
        return activityService.getActivitiesByCollectivity(id);
    }
}
