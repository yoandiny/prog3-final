package mg.yoan.finaltd.controller;

import lombok.RequiredArgsConstructor;
import mg.yoan.finaltd.dto.AttendanceRequest;
import mg.yoan.finaltd.entity.Attendance;
import mg.yoan.finaltd.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/activities/{activityId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public void markAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<AttendanceRequest> requests) {
        attendanceService.markAttendance(id, activityId, requests);
    }

    @GetMapping
    public List<Attendance> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        return attendanceService.getAttendanceByActivity(activityId);
    }
}
