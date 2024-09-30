


import React from 'react';
import { Task } from '@/app/lib/types/courses';
import { TrashIcon } from '@heroicons/react/24/outline';
import { Button } from '@/app/ui/button';

interface TaskEditProps {
    task: Task;
    taskIndex: number;
    topicIndex: number;
    handleTaskChange: (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
        topicIndex: number,
        taskIndex: number,
    ) => void;
    deleteTask: (topicIndex: number, taskIndex: number) => void;
    handleOptionChange: (
        optionKey: string,
        topicIndex: number,
        taskIndex: number,
        isChecked: boolean
    ) => void;
}


const bloomLevels = [
    'NONE',
    'REMEMBERING',
    'UNDERSTANDING',
    'APPLYING',
    'ANALYZING',
    'EVALUATING',
    'CREATING',
];


const taskTypes = [
    'MULTIPLE_CHOICE',
    'DRAG_AND_DROP',
    'MATCHING',
    'SORTING',
    'FREE_TEXT',
    'SHORT_ANSWER',
    'FILL_IN_THE_BLANKS',
    'TRUE_FALSE',
    'CODE_COMPLETION',
    'ESSAY',
    'CODING',
    'CODE_UNDERSTANDING'
];

const TaskEdit: React.FC<TaskEditProps> = ({
                                               task,
                                               taskIndex,
                                               topicIndex,
                                               handleTaskChange,
                                               deleteTask,
                                               handleOptionChange,
                                           }) => {
    // For multiple-choice tasks, parse correctAnswer into an array of selected options
    let selectedOptions:any;
    try {
        console.log(task.correctAnswer);
         selectedOptions = task.correctAnswer ? JSON.parse(task.correctAnswer) : {};
    }catch (e) {
        console.log(e)
    }



    return (
        <div className="mb-2 rounded-md bg-gray-100 p-4">
            <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold">{`Task ${taskIndex + 1}`}</h3>
                <button
                    type="button"
                    onClick={() => deleteTask(topicIndex, taskIndex)}
                    className="text-red-600 hover:text-red-800"
                >
                    <TrashIcon className="h-5 w-5" />
                </button>
            </div>
            <div className="mt-2">
                <label className="block text-sm font-medium">Question</label>
                <textarea
                    name="question"
                    value={task.question}
                    onChange={(e) => handleTaskChange(e, topicIndex, taskIndex)}
                    className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                />
            </div>
            <div className="mt-2">
                <label className="block text-sm font-medium">Bloom Level</label>
                <select
                    name="bloomLevel"
                    value={task.bloomLevel || ''}
                    onChange={(e) => handleTaskChange(e, topicIndex, taskIndex)}
                    className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                >
                    <option value="">Select Bloom Level</option>
                    {bloomLevels.map((level) => (
                        <option key={level} value={level}>
                            {level}
                        </option>
                    ))}
                </select>
            </div>
            <div className="mt-2">
                <label className="block text-sm font-medium">Task Type</label>
                <select
                    name="taskType"
                    value={task.taskType || ''}
                    onChange={(e) => handleTaskChange(e, topicIndex, taskIndex)}
                    className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                >
                    <option value="">Select Task Type</option>
                    {taskTypes.map((type) => (
                        <option key={type} value={type}>
                            {type.replace(/_/g, ' ')}
                        </option>
                    ))}
                </select>
            </div>
            {task.taskType === 'MULTIPLE_CHOICE' && task.options && (
                <div className="mt-4">
                    <label className="block text-sm font-medium">Options</label>
                    {Object.entries(task.options).map(([optionKey, optionValue], index) => (
                        <div key={index} className="mt-2 flex items-center">
                            <input
                                type="checkbox"
                                checked={selectedOptions[optionKey] || false}
                                onChange={(e) =>
                                    handleOptionChange(optionKey, topicIndex, taskIndex, e.target.checked)
                                }
                                className="mr-2"
                            />
                            <label className="text-sm font-medium">{optionValue}</label>
                        </div>
                    ))}
                </div>
            )}
            <div className="mt-2">
                <label className="block text-sm font-medium">
                    {task.taskType === 'MULTIPLE_CHOICE' ? 'Correct Answers' : 'Correct Answer'}
                </label>
                {task.taskType === 'MULTIPLE_CHOICE' ? (
                    <div>
                        {/* Display selected correct answers */}
                        {Object.entries(selectedOptions)
                            .filter(([key, value]) => value)
                            .map(([key]) => (
                                <div key={key} className="text-sm">
                                    {task.options![key]}
                                </div>
                            ))}
                    </div>
                ) : (
                    <textarea
                        name="correctAnswer"
                        value={task.correctAnswer || ''}
                        onChange={(e) => handleTaskChange(e, topicIndex, taskIndex)}
                        className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                    />
                )}
            </div>
        </div>
    );
};

export default TaskEdit;
