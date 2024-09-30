"use client";
import React from 'react';
import { Topic } from '@/app/lib/types/courses';
import { PlusIcon, TrashIcon, BookmarkIcon } from "@heroicons/react/24/outline";
import { Button } from "@/app/ui/button";
import LearningGoalsSection from "@/app/ui/courses/editCourse/LearningGoalSection";

interface TopicsSectionProps {
    topics: Topic[];
    newTopics: Topic[];
    handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>, index?: number, isNewTopic?: boolean) => void;
    saveTopic: (topic: Topic, isNewTopic: boolean, topicIndex: number) => void;
    deleteTopic: (index: number, isNewTopic: boolean) => void;
    addLearningGoal: (topicIndex: number, isNewTopic: boolean) => void;
    deleteLearningGoal: (topicIndex: number, goalIndex: number, isNewTopic: boolean) => void;
    saveLearningGoal: (learningGoal: any, topicIndex: number, goalIndex: number, isNewTopic: boolean) => void;
    addTopic: () => void;
    handleGoalChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, goalIndex: number, topicIndex: number, isNewTopic?: boolean) => void;
}

const TopicsSection: React.FC<TopicsSectionProps> = ({
                                                         topics,
                                                         newTopics,
                                                         handleChange,
                                                         saveTopic,
                                                         deleteTopic,
                                                         addLearningGoal,
                                                         deleteLearningGoal,
                                                         saveLearningGoal,
                                                         addTopic,
                                                         handleGoalChange,
                                                     }) => {
    // Sort topics by orderNumber (or index if orderNumber is null)
    const sortedTopics = [...topics].sort((a, b) => {
        if (a.orderNumber !== null && b.orderNumber !== null) {
            return a.orderNumber - b.orderNumber;
        } else if (a.orderNumber !== null) {
            return -1;
        } else if (b.orderNumber !== null) {
            return 1;
        }
        return 0;
    });

    return (
        <div className="topics-section mb-6">
            <Button
                type="button"
                onClick={addTopic}
                className="flex items-center space-x-2 mt-4"
            >
                <PlusIcon className="h-5 w-5 text-white" />
                <span>Themenbereich hinzufügen</span>
            </Button>
            {sortedTopics.map((topic, index) => (
                <div key={topic.id || `new-${index}`} className="topic-item mb-4 p-4 rounded-md border border-gray-200">
                    <div className="flex justify-between items-center">
                        <h3 className="text-lg font-semibold">
                            {topic.orderNumber !== null ? `${topic.orderNumber}.` : `#${index + 1}`} {topic.name}
                            {topic.maxBloom && (
                                <span
                                    className="ml-2 inline-flex items-center px-3 py-0.5 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                                    {topic.maxBloom}
                                </span>
                            )}
                        </h3>
                        <div className="flex items-center space-x-2">
                            <Button
                                type="button"
                                onClick={() => saveTopic(topic, false, index)}
                                className="text-green-600 hover:text-green-800 flex items-center"
                            >
                                <BookmarkIcon className="h-5 w-5"/>
                                <span>Speichern</span>
                            </Button>
                            <Button
                                type="button"
                                onClick={() => deleteTopic(index, false)}
                                className="text-red-600 hover:text-red-800 flex items-center"
                            >
                                <TrashIcon className="h-5 w-5"/>
                                <span>Löschen</span>
                            </Button>
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`topic-orderNumber-${index}`} className="mb-2 block text-sm font-medium">
                            Reihenfolge Nummer
                        </label>
                        <div className="relative mt-2 rounded-md">
                            <input
                                id={`topic-orderNumber-${index}`}
                                name={`topic-orderNumber`}
                                type="number"
                                value={topic.orderNumber!}
                                onChange={(e) => handleChange(e, index)}
                                placeholder="Enter order number of topic"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                            />
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`topic-bloom-${index}`} className="mb-2 block text-sm font-medium">
                            Bloom Stufe
                        </label>
                        <div className="relative mt-2 rounded-md">
                            <input
                                id={`topic-bloom-${index}`}
                                name={`topic-bloom`}
                                type="text"
                                value={topic.maxBloom!}
                                onChange={(e) => handleChange(e, index)}
                                placeholder="Wähle die Bloom stufe"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                            />
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`topic-name-${index}`} className="mb-2 block text-sm font-medium">
                            Name
                        </label>
                        <div className="relative mt-2 rounded-md">
                            <input
                                id={`topic-name-${index}`}
                                name={`topic-name`}
                                type="text"
                                value={topic.name}
                                onChange={(e) => handleChange(e, index)}
                                placeholder="Enter name of topic"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                            />
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`topic-description-${index}`} className="mb-2 block text-sm font-medium">
                            Beschreibung
                        </label>
                        <div className="relative mt-2 rounded-md">
                            <textarea
                                id={`topic-description-${index}`}
                                name={`topic-description`}
                                value={topic.description || ''}
                                onChange={(e) => handleChange(e, index)}
                                placeholder="Enter description of topic"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                            />
                        </div>
                    </div>
                    <LearningGoalsSection
                        learningGoals={topic.learningGoals}
                        handleGoalChange={handleGoalChange}
                        topicIndex={index}
                        isNewTopic={false}
                        deleteLearningGoal={deleteLearningGoal}
                        addLearningGoal={addLearningGoal}
                        saveLearningGoal={saveLearningGoal}
                    />
                </div>
            ))}
            {newTopics.map((topic, index) => (
                <div
                    key={`new-${index}`}
                    id={`new-topic-${index}`}  // Add an ID for scrolling
                    className="topic-item mb-4 p-4 rounded-md border border-blue-300 bg-blue-50"
                >
                    <div className="flex justify-between items-center">
                        <h3 className="text-lg font-semibold">
                            Topic {topics.length + index + 1} <span className="text-xs text-blue-500">(New)</span>
                            {topic.maxBloom && (
                                <span className="ml-2 inline-flex items-center px-3 py-0.5 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
            {topic.maxBloom}
          </span>
                            )}
                        </h3>
                        <div className="flex items-center space-x-2">
                            <Button
                                type="button"
                                onClick={() => saveTopic(topic, true, index)}
                                className="text-green-600 hover:text-green-800 flex items-center"
                            >
                                <BookmarkIcon className="h-5 w-5" />
                                <span>Save</span>
                            </Button>
                            <Button
                                type="button"
                                onClick={() => deleteTopic(index, true)}
                                className="text-red-600 hover:text-red-800 flex items-center"
                            >
                                <TrashIcon className="h-5 w-5" />
                                <span>Delete</span>
                            </Button>
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`new-topic-name-${index}`} className="mb-2 block text-sm font-medium">
                            Topic Name <span className="text-xs text-blue-500">(New)</span>
                        </label>
                        <div className="relative mt-2 rounded-md">
                            <input
                                id={`new-topic-name-${index}`}
                                name={`topic-name`}
                                type="text"
                                value={topic.name}
                                onChange={(e) => handleChange(e, index, true)}
                                placeholder="Enter name of topic"
                                className="peer block w-full rounded-md border border-blue-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                            />
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor={`new-topic-description-${index}`} className="mb-2 block text-sm font-medium">
                            Topic Description <span className="text-xs text-blue-500">(New)</span>
                        </label>
                        <div className="relative mt-2 rounded-md">
        <textarea
            id={`new-topic-description-${index}`}
            name={`topic-description`}
            value={topic.description || ''}
            onChange={(e) => handleChange(e, index, true)}
            placeholder="Enter description of topic"
            className="peer block w-full rounded-md border border-blue-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
        />
                        </div>
                    </div>
                    <LearningGoalsSection
                        learningGoals={topic.learningGoals}
                        handleGoalChange={handleGoalChange}
                        topicIndex={index}
                        isNewTopic={true}
                        deleteLearningGoal={deleteLearningGoal}
                        addLearningGoal={addLearningGoal}
                        saveLearningGoal={saveLearningGoal}
                    />
                </div>
            ))}

        </div>
    );
};

export default TopicsSection;
