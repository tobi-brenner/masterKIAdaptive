"use client"
import { useState } from 'react';
import { ArrowDownIcon, ArrowUpIcon } from '@heroicons/react/24/outline';

const TopicCollapse = ({topics} : {topics: any}) => {
    const [showTopics, setShowTopics] = useState(false);

    const toggleTopics = () => {
        setShowTopics(!showTopics);
    };

    return (
        <div>
            <div onClick={toggleTopics} className="flex justify-between items-center cursor-pointer px-4 py-2 bg-gray-200 mt-3">
                <span>Themen anzeigen</span>
                {showTopics ? <ArrowUpIcon className="h-5 w-5" /> : <ArrowDownIcon className="h-5 w-5" />}
            </div>
            {showTopics && (
                <ul className="list-disc space-y-1 p-4">
                    {topics.map((topic: any, i: any) => (
                        <li key={i}>{topic.name}</li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default TopicCollapse;