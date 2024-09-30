


import React from 'react';
import { Topic } from '@/app/lib/types/courses';
import { Button } from '@/app/ui/button';
import { PlusIcon } from '@heroicons/react/24/outline';
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import TaskEdit from "./TaskEdit";

interface TaskSectionProps {
    topics: Topic[];
    handleTaskChange: (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
        topicIndex: number,
        taskIndex: number,
    ) => void;
    addTask: (topicIndex: number) => void;
    deleteTask: (topicIndex: number, taskIndex: number) => void;
    generateTasksForTopic: (topicIndex: number, keyword: string) => void;
    handleOptionChange: (
        optionKey: string,
        topicIndex: number,
        taskIndex: number,
        isChecked: boolean
    ) => void;
}

const TaskSection: React.FC<TaskSectionProps> = ({
                                                     topics,
                                                     handleTaskChange,
                                                     addTask,
                                                     deleteTask,
                                                     generateTasksForTopic,
                                                     handleOptionChange
                                                 }) => {
    console.log(topics)
    return (
        <div>
            {topics.map((topic, topicIndex) => (
                <div key={topic.id || topicIndex} className="mb-6">
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <div className="rounded-md border border-gray-300 shadow-md">
                                <div
                                    className="flex items-center justify-between px-4 py-2 cursor-pointer"
                                    aria-expanded="false"
                                >
                                    <h3 className="text-lg font-semibold text-gray-800 underline">
                                        {`Topic: ${topic.name}`}
                                    </h3>
                                    <button type="button" className="text-gray-600 hover:text-gray-800">
                    <span className="inline-block transform transition-transform duration-200">
                      ↓
                    </span>
                                    </button>
                                </div>
                            </div>
                        </CollapsibleTrigger>
                        <CollapsibleContent className="p-4 border border-t-0 border-gray-300 rounded-b-md shadow-md">
                            <div className="mb-4 flex justify-end">
                                <Button
                                    type="button"
                                    onClick={() => generateTasksForTopic(topicIndex, 'further')}
                                    className="bg-blue-500 text-white"
                                >
                                    Generate further Tasks
                                </Button>
                            </div>
                            {topic.tasks?.map((task, taskIndex) => (
                                <TaskEdit
                                    key={task.id || taskIndex}
                                    task={task}
                                    taskIndex={taskIndex}
                                    topicIndex={topicIndex}
                                    handleTaskChange={handleTaskChange}
                                    deleteTask={deleteTask}
                                    handleOptionChange={handleOptionChange}
                                />
                            ))}
                            <Button
                                type="button"
                                onClick={() => addTask(topicIndex)}
                                className="mt-4 flex items-center space-x-2"
                            >
                                <PlusIcon className="h-5 w-5 text-white" />
                                <span>Add Task</span>
                            </Button>
                        </CollapsibleContent>
                    </Collapsible>
                </div>
            ))}
        </div>
    );
};

export default TaskSection;
