package router.server.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import router.publish.EndEvent;
import router.publish.ErrorEvent;
import router.publish.StartEvent;
import router.server.publish.ProgressTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
public class ProgressService {
    List<ProgressTask> currentTasks = new ArrayList<>();

    public ProgressTask registryTask(StartEvent eventPackage) {
        if (eventPackage.getTaskCount() == 0) {
            return null;
        }
        ProgressTask progressTask = new ProgressTask();
        progressTask.setId(eventPackage.getUniqId());
        progressTask.setTaskName(eventPackage.getTaskName());
        progressTask.setCount(eventPackage.getTaskCount());
        progressTask.setCurrent(0);
        progressTask.setMessage("");
        currentTasks.add(progressTask);
        return progressTask;
    }

    public ProgressTask updateTask(EndEvent eventPackage) {
        Optional<ProgressTask> first = currentTasks.stream().filter(task -> task.getId().equals(eventPackage.getUniqId())).findFirst();
        if (first.isPresent()) {
            ProgressTask progressTask = first.get();
            progressTask.updateCount();
            progressTask.setMessage(eventPackage.getMessage());
            return progressTask;
        }
        throw new RuntimeException("No progress task found for event type: " + eventPackage.getType().name());
    }

    public ProgressTask handlerErrorTask(ErrorEvent errorEvent) {
        Optional<ProgressTask> first = currentTasks.stream().filter(task -> task.getId().equals(errorEvent.getUniqId())).findFirst();
        if (first.isPresent()) {
            ProgressTask progressTask = first.get();
            progressTask.setFail(true);
            System.out.println(errorEvent.getType().name());
            System.out.printf(errorEvent.getErrorType().name());
            errorEvent.getException().printStackTrace();
            progressTask.setError(errorEvent.getException().getMessage());
            return progressTask;
        }
        throw new RuntimeException("No progress task found for event type: " + errorEvent.getType().name());
    }

    public void clearProgress() {
        currentTasks.clear();
    }
}
