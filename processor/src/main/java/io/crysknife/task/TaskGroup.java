/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.task;

import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.logger.TreeLogger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/9/21
 */
public class TaskGroup implements Task {

  private final Set<Task> tasks = new LinkedHashSet<>();
  private final TreeLogger logger;

  public TaskGroup(TreeLogger logger) {
    this.logger = logger;
  }

  @Override
  public void execute() {
    Set<UnableToCompleteException> errors = new LinkedHashSet<>();
    for (Task task : tasks) {
      try {
        long start = System.currentTimeMillis();
        task.execute();


        logger.log(TreeLogger.INFO, "Finished task " + task.getClass().getSimpleName() + " in "
            + (System.currentTimeMillis() - start) + "ms");

      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    }
    if (!errors.isEmpty()) {
      for (UnableToCompleteException error : errors) {
        if (error.errors != null) {
          for (UnableToCompleteException unableToCompleteException : error.errors) {
            logger.log(TreeLogger.ERROR, unableToCompleteException.getMessage());
          }
        } else {
          logger.log(TreeLogger.ERROR, error.getMessage());
        }
      }
      throw new GenerationException();
    }
  }

  public void addTask(Task task) {
    tasks.add(task);
  }
}
