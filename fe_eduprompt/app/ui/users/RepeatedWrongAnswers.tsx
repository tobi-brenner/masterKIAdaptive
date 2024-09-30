

"use client";
import React, { useEffect, useState } from 'react';
import { Badge } from "@/components/ui/badge";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"; // Adjust the path if necessary


interface Props {
    userId?: string;
    learningPathId?: string;
}

interface TaskUserAttemptDTO {
    taskId: number;
    question: string;
    taskType: string;
    userTaskAttempt: UserTaskAttemptDTO;
}

interface TaskUserAttemptsCountDTO {
    task: Task;
    userTaskAttempts: UserTaskAttemptDTO[];
    count: number;
}

interface Task {
    id: number;
    question: string;
    taskType: string;
}

interface UserTaskAttemptDTO {
    id: number;
    isCorrect: boolean;
    attemptTime: string;
    answer: {
        answer: string;
        feedback: string;
    };
}

export default function RepeatedWrongAnswers({ userId, learningPathId }: Props) {
    const [tasksWrongOnce, setTasksWrongOnce] = useState<TaskUserAttemptDTO[]>([]);
    const [tasksWrongMultiple, setTasksWrongMultiple] = useState<TaskUserAttemptsCountDTO[]>([]);

    useEffect(() => {
        if (userId && learningPathId) {
            fetchTasks('wrong-once', setTasksWrongOnce);
            fetchTasks('wrong-multiple', setTasksWrongMultiple);
        }
    }, [userId, learningPathId]);

    const fetchTasks = async (type: string, setter: React.Dispatch<React.SetStateAction<any[]>>) => {
        try {
            const endpoint = `/attempts/user/${userId}/learningpath/${learningPathId}/${type}`;
            const data = await fetchFromAPI<any[]>(endpoint);
            setter(data);
        } catch (error) {
            console.error(`Failed to fetch tasks for ${type}:`, error);
        }
    };

    return (
        // <div>
        <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
            {/*<Tabs defaultValue="wrong-once" className="w-[400px]">*/}
            <Tabs defaultValue="wrong-once" className="w-4/4">
                <TabsList>
                    <TabsTrigger value="wrong-once">Einmalig falsch</TabsTrigger>
                    <TabsTrigger value="wrong-multiple">Mehrfach falsch</TabsTrigger>
                </TabsList>
                <TabsContent value="wrong-once">
                    <TaskList tasks={tasksWrongOnce} />
                </TabsContent>
                <TabsContent value="wrong-multiple">
                    <TaskListMultiple tasks={tasksWrongMultiple} />
                </TabsContent>
            </Tabs>
        </div>
    );
}

function TaskList({ tasks }: { tasks: TaskUserAttemptDTO[] }) {
    if (tasks.length === 0) {
        return <div>No tasks found.</div>;
    }

    return (
        <ul>
            {tasks.map(({ taskId, question, taskType, userTaskAttempt }) => (
                <li key={taskId} className="mb-4 p-4 border rounded-md">
                    {/*<div>*/}
                    <div className="mb-2 flex items-center">
                        <Badge className="mr-2">{taskType}</Badge>
                        <p>{question}</p>
                    </div>
                    <p>Attempt Time: {new Date(userTaskAttempt.attemptTime).toLocaleString()}</p>
                    <p>Answer: {userTaskAttempt.answer?.answer}</p>
                    <p>Feedback: {userTaskAttempt.answer?.feedback}</p>
                </li>
            ))}
        </ul>
    );
}

function TaskListMultiple({ tasks }: { tasks: TaskUserAttemptsCountDTO[] }) {
    if (tasks.length === 0) {
        return <div>No tasks found.</div>;
    }

    return (
        <ul>
            {tasks.map(({ task, userTaskAttempts, count }) => (
                <li key={task.id} className="mb-4 p-4 border rounded-md">
                    <div className="mb-2 flex items-center">
                        <Badge className="mr-2">{task.taskType}</Badge>
                        <p>{task.question}</p>
                    </div>
                    <p>Wrong Attempts: {count}</p>
                    {userTaskAttempts.map((attempt, index) => (
                        <div key={attempt.id} className="mb-2">
                            <p>Attempt {index + 1} Time: {new Date(attempt.attemptTime).toLocaleString()}</p>
                            <p>Answer: {attempt.answer?.answer}</p>
                            <p>Feedback: {attempt.answer?.feedback}</p>
                        </div>
                    ))}
                </li>
            ))}
        </ul>
    );
}








