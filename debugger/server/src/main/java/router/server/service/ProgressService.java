package router.server.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import router.publish.EventPackage;
import router.publish.EventType;
import router.server.publish.ProgressTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
public class ProgressService {
    List<ProgressTask> currentTasks = new ArrayList<ProgressTask>();

    public ProgressTask registryTask(EventType eventType, EventPackage eventPackage) {
        ProgressTask progressTask = new ProgressTask();
        progressTask.setId(eventPackage.getId());
        progressTask.setTaskName(eventType.name());
        progressTask.setCount((Integer) eventPackage.getMsg());
        progressTask.setCurrent(0);
        currentTasks.add(progressTask);
        return progressTask;
    }

    public ProgressTask updateTask(EventType eventType, EventPackage eventPackage) {
        Optional<ProgressTask> first = currentTasks.stream().filter(task -> task.getId().equals(eventPackage.getId())).findFirst();
        if (first.isPresent()) {
            ProgressTask progressTask = first.get();
            progressTask.updateCount();
            return progressTask;
        }
        throw new RuntimeException("No progress task found for event type: " + eventType.name());
    }

}
