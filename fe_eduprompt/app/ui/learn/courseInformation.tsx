
import { BE_BASE_URL } from "@/constants";
import { Course, Topic } from "@/app/lib/types/courses";

interface CourseInformationProps {
    course: Course;
}

const CourseInformation: React.FC<CourseInformationProps> = ({ course }) => {
    const baseUrl = `${BE_BASE_URL}/files/download`;

    if (!course) {
        return <p className="text-gray-700">Keine Kursinformationen verfügbar.</p>;
    }

    return (
        <section className="mb-8">
            <h2 className="text-xl font-bold mb-4">Kursinformationen: {course.subject}</h2>
            <p>{course.description}</p>

            {course.topics && course.topics.length > 0 ? (
                <div className="mt-6">
                    <h3 className="text-lg font-bold mb-4">Themen</h3>
                    {course.topics.map((topic: Topic, index: number) => (
                        <div key={index} className="mb-6 p-4 border rounded-lg bg-gray-50">
                            <h4 className="text-md font-semibold mb-2">{topic.name}</h4>
                            <p className="text-sm text-gray-700 mb-2">{topic.description}</p>

                            {topic.learningGoals && topic.learningGoals.length > 0 && (
                                <div className="ml-4">
                                    <h5 className="text-sm font-semibold mb-2">Lernziele:</h5>
                                    <ul className="list-disc list-inside">
                                        {topic.learningGoals.map((goal, goalIndex) => (
                                            <li key={goalIndex} className="text-sm text-gray-600">{goal.goal}</li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            ) : (
                <p className="text-gray-700 mt-4">Keine Themen verfügbar.</p>
            )}
        </section>
    );
};

export default CourseInformation;
