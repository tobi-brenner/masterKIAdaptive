'use client';
import {Assessment, AttemptData, LearningPath, LearningStep, Task} from "@/app/lib/types/courses";
import {Badge} from "@/components/ui/badge";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import {Label} from "@/components/ui/label";
import React, {useEffect, useState} from "react";
import {Attempt} from "@/app/lib/types/users";
import {submitTaskAttempt} from "@/app/lib/api/courses/task";
import {AttemptResponse} from "@/app/lib/types/tasks";


interface TaskCardProps {
    task: Task;
    attempt?: Attempt | undefined;
    user?: any;
    assessment?: Assessment;
    learningStep?: LearningStep;
    learningPath: LearningPath;
    showFeedback: boolean;
    onTaskCompleted: (attempt: Attempt) => void;
}


export default function TaskCard({task, attempt, user, assessment, learningStep, learningPath, showFeedback, onTaskCompleted}: TaskCardProps) {
    const [answer, setAnswer] = useState(attempt ? attempt.answer : '');
    const [feedback, setFeedback] = useState(attempt ? attempt.feedback : '');
    const [isCorrect, setIsCorrect] = useState(attempt ? attempt.isCorrect : null);
    const [isDisabled, setIsDisabled] = useState(!!attempt);
    console.log("Task", task)
    console.log("attempt", attempt)

    useEffect(() => {
        if (attempt) {
            setAnswer(attempt.answer);
            setFeedback(attempt.feedback);
            setIsCorrect(attempt.isCorrect);
            setIsDisabled(true);
        }
    }, [attempt]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setAnswer(e.target.value);
    };

    const handleSubmit = async () => {
        try {

            const finalAnswer = !answer || answer.trim() === "" ? "-" : answer;

            const response = await submitTaskAttempt({
                userId: user.id,
                taskId: task.id!,
                answer: finalAnswer,
                assessmentId: assessment ? assessment.id : null,
                learningStepId: learningStep ? learningStep.id : null,
                learningPathId: learningPath.id,
                isCorrect: false
            });
            setFeedback(response.feedback);
            setIsCorrect(response.isCorrect);
            setAnswer(response.answer);

            const attempt = transformAttemptResponse(response);
            onTaskCompleted(attempt);
        } catch (error) {
            console.error('Error submitting task:', error);
        }
    };
    const transformAttemptResponse = (response: AttemptResponse): Attempt => {
        return {
            id: response.id.toString(),
            taskId: response.task.id,
            userId: response.user.id.toString(),
            isCorrect: response.isCorrect,
            attemptTime: response.attemptTime,
            feedback: response.feedback,
            answer: response.answer,
            task: {
                id: response.task.id,
                question: response.task.question,
                taskType: response.task.taskType,
                topic: response.task.topic
            },
            assessmentId: assessment ? assessment.id : undefined,
            learningStepId: learningStep ? learningStep.id : undefined,
        };
    };


    const renderOptions = () => {
        if (task.taskType === 'MULTIPLE_CHOICE') {
            return (
                <div>
                    {Object.entries(task.options!).map(([key, value]) => (
                        <div key={key} className="flex items-center mb-2">
                            <input
                                type="radio"
                                name={`task-${task.id}`}
                                value={value}
                                className="mr-2"
                                onChange={handleInputChange}
                                checked={answer === value}
                                disabled={isDisabled}
                            />
                            <label>{value}</label>
                        </div>
                    ))}
                </div>
            );
        } else if (task.taskType === 'TRUE_FALSE') {
            return (
                <div className="flex items-center mb-2">
                    <div className="flex items-center space-x-2">
                        <input
                            type="radio"
                            name={`task-${task.id}`}
                            value="true"
                            className="mr-2"
                            onChange={handleInputChange}
                            checked={answer === "true"}
                            disabled={isDisabled}
                        />
                        <label>True</label>
                    </div>
                    <div className="flex items-center space-x-2">
                        <input
                            type="radio"
                            name={`task-${task.id}`}
                            value="false"
                            className="mr-2"
                            onChange={handleInputChange}
                            checked={answer === "false"}
                            disabled={isDisabled}
                        />
                        <label>False</label>
                    </div>
                </div>
            );
        }
    };

    const renderInput = () => {
        if (task.taskType === 'SHORT_ANSWER' || task.taskType === 'FREE_TEXT' || task.taskType === 'ESSAY'|| task.taskType === 'CODING' || task.taskType === 'CODE_UNDERSTANDING') {
            return (
                <textarea
                    name={`task-${task.id}`}
                    className="w-full p-2 border rounded-md"
                    rows={4}
                    onChange={handleInputChange}
                    value={answer}
                    disabled={isDisabled}
                />
            );
        }
    };

    return (
        <div className="task-card p-4 border rounded-md shadow-md mb-4">
            <span
                className="ml-2 inline-flex items-center px-3 py-0.5 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                {task.taskType}
            </span>
            {/*<Badge className={"mb-2"}>{task.topicId}</Badge>*/}
            <p className="mb-4">{task.question}</p>
            {renderOptions()}
            {renderInput()}
            <button
                className="mt-4 rounded-md bg-blue-500 px-4 py-2 text-sm text-white transition-colors hover:bg-blue-400"
                onClick={handleSubmit}
                disabled={isDisabled}
            >
                Bestätigen
            </button>
            {/*{feedback && (*/}
            {/*    <div className={`feedback mt-2 ${isCorrect ? 'text-green-500' : 'text-red-500'}`}>*/}
            {/*        {feedback}*/}
            {/*    </div>*/}
            {/*)}*/}
            {feedback && (
                <div
                    className={`feedback mt-2 p-2 rounded ${isCorrect ? 'bg-green-400 text-white' : 'bg-red-400 text-white'}`}>
                    {feedback}
                </div>
            )}
        </div>
    );
}