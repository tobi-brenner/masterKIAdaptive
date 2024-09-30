


import React from 'react';
import { LearningGoal } from '@/app/lib/types/courses';
import { PlusIcon, TrashIcon, BookmarkIcon, ChevronDownIcon, ChevronRightIcon } from "@heroicons/react/24/outline";
import { Button } from "@/app/ui/button";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible";

interface LearningGoalsSectionProps {
    learningGoals: LearningGoal[];
    handleGoalChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, goalIndex: number, topicIndex: number, isNewTopic?: boolean) => void;
    topicIndex: number;
    isNewTopic?: boolean;
    deleteLearningGoal: (topicIndex: number, goalIndex: number, isNewTopic: boolean) => void;
    addLearningGoal: (topicIndex: number, isNewTopic: boolean) => void;
    saveLearningGoal: (learningGoal: any, topicIndex: number, goalIndex: number, isNewTopic: boolean) => void;
}

const LearningGoalsSection: React.FC<LearningGoalsSectionProps> = ({
                                                                       learningGoals,
                                                                       handleGoalChange,
                                                                       topicIndex,
                                                                       isNewTopic,
                                                                       deleteLearningGoal,
                                                                       addLearningGoal,
                                                                       saveLearningGoal,
                                                                   }) => {
    const [isOpen, setIsOpen] = React.useState(true);

    const toggleOpen = () => {
        setIsOpen(!isOpen);
    };

    return (
        <div className="learning-goals-section p-4 border border-gray-400 bg-gray-100 rounded-md mt-2">
            <Collapsible>
                <CollapsibleTrigger
                    className="flex items-center space-x-2 mb-2 block text-sm font-medium bg-gray-100 p-2 rounded-md cursor-pointer"
                    onClick={toggleOpen}
                >
                    {isOpen ? <ChevronDownIcon className="h-5 w-5" /> : <ChevronRightIcon className="h-5 w-5" />}
                    <span>{isOpen ? 'Lernziele ausblenden' : 'Lernziele anzeigen'}</span>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <Button
                        type="button"
                        onClick={() => addLearningGoal(topicIndex, isNewTopic!)}
                        className="flex items-center space-x-2 mt-4"
                    >
                        <PlusIcon className="h-5 w-5 text-white" />
                        <span>Lernziel hinzufügen</span>
                    </Button>
                    {learningGoals.map((goal, goalIndex) => (
                        <div key={goal.id || `newGoal-${goalIndex}`} className="mb-4 p-4 rounded-md border border-gray-200 bg-gray-100">
                            <div className="flex justify-between items-center">
                                <h4 className="text-md font-semibold">Learning Goal {goalIndex + 1}</h4>
                                <div className="flex items-center space-x-2">
                                    <Button
                                        type="button"
                                        onClick={() => saveLearningGoal(goal, topicIndex, goalIndex, isNewTopic!)}
                                        className="text-green-600 hover:text-green-800 flex items-center"
                                    >
                                        <BookmarkIcon className="h-5 w-5" />
                                        <span>Speichern</span>
                                    </Button>
                                    <Button
                                        type="button"
                                        onClick={() => deleteLearningGoal(topicIndex, goalIndex, isNewTopic!)}
                                        className="text-red-600 hover:text-red-800 flex items-center"
                                    >
                                        <TrashIcon className="h-5 w-5" />
                                        <span>Löschen</span>
                                    </Button>
                                </div>
                            </div>
                            <div className="mb-2">
                                {/*<p>{goal.maxBloom}</p>*/}
                                <label htmlFor={`goal-goal-${topicIndex}-${goalIndex}`} className="block text-sm font-medium">
                                    Lernziel
                                </label>
                                <input
                                    id={`goal-goal-${topicIndex}-${goalIndex}`}
                                    name={`goal-goal`}
                                    type="text"
                                    value={goal.goal || ''}
                                    onChange={(e) => handleGoalChange(e, goalIndex, topicIndex, isNewTopic)}
                                    placeholder="Enter goal"
                                    className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                                />
                            </div>
                            <div className="mb-2">
                                <label htmlFor={`goal-description-${topicIndex}-${goalIndex}`} className="block text-sm font-medium">
                                    Beschreibung
                                </label>
                                <textarea
                                    id={`goal-description-${topicIndex}-${goalIndex}`}
                                    name={`goal-description`}
                                    value={goal.description || ''}
                                    onChange={(e) => handleGoalChange(e, goalIndex, topicIndex, isNewTopic)}
                                    placeholder="Enter description"
                                    className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                                />
                            </div>
                        </div>
                    ))}
                </CollapsibleContent>
            </Collapsible>
        </div>
    );
}

export default LearningGoalsSection;
