/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.rule;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.raml.parser.visitor.IncludeInfo;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class ValidationResult
{

    public enum Level
    {
        ERROR, WARN, INFO
    }

    private Level level;
    private String message;
    private Mark startMark;
    private Mark endMark;
    private Deque<IncludeInfo> includeContext;

    private ValidationResult(Level level, String message, Mark startMark, Mark endMark)
    {
        this.level = level;
        this.message = message;
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public boolean isValid()
    {
        return level != Level.ERROR;
    }

    public String getMessage()
    {
        return message;
    }

    public static ValidationResult createErrorResult(String message, Mark startMark, Mark endMark)
    {
        return new ValidationResult(Level.ERROR, message, startMark, endMark);
    }

    public static ValidationResult createErrorResult(String message, Node node)
    {
        return createErrorResult(message, node.getStartMark(), node.getEndMark());
    }

    public static ValidationResult createErrorResult(String message)
    {
        return createErrorResult(message, null, null);
    }

    public static ValidationResult create(Level level, String message)
    {
        return new ValidationResult(level, message, null, null);
    }

    public Mark getStartMark()
    {
        return startMark;
    }

    public Mark getEndMark()
    {
        return endMark;
    }

    public String getIncludeName()
    {
        if (includeContext.isEmpty())
        {
            return null;
        }
        return includeContext.peek().getIncludeName();
    }

    public Deque<IncludeInfo> getIncludeContext()
    {
        return includeContext;
    }

    public void setIncludeContext(Deque<IncludeInfo> includeContext)
    {
        this.includeContext = new ArrayDeque<IncludeInfo>(includeContext);
    }

    public static boolean areValid(List<ValidationResult> validationResults)
    {
        for (ValidationResult result : validationResults)
        {
            if (!result.isValid())
            {
                return false;
            }
        }
        return true;
    }

    public static List<ValidationResult> getLevel(Level level, List<ValidationResult> results)
    {
        List<ValidationResult> filtered = new ArrayList<ValidationResult>();
        for (ValidationResult result : results)
        {
            if (result.level == level)
            {
                filtered.add(result);
            }
        }
        return filtered;
    }

    @Override
    public String toString()
    {
        return "ValidationResult{" +
               "level=" + level +
               ", message='" + message + '\'' +
               '}';
    }

}
