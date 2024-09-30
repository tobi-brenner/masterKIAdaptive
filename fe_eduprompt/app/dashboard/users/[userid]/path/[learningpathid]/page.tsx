import Breadcrumbs from '@/app/ui/invoices/breadcrumbs';
import {fetchLearningPath} from "@/app/lib/api/courses/course";
import {BloomLevel, Feedback, LearningPath, LearningStep} from "@/app/lib/types/courses";
import {Attempt} from "@/app/lib/types/users";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import RepeatedWrongAnswers from "@/app/ui/users/RepeatedWrongAnswers";
import BurndownChart from "@/app/ui/learn/burndownChart";
import LearningProgress from "@/app/ui/learn/learningProgress";
import {fetchFeedbacks, fetchLearningStatistics} from "@/app/lib/api/courses/learningpath";
import FeedbackList from "@/app/ui/learn/feedbackList";
import FeedbackForm from "@/app/ui/learn/feedbackForn";
import FeedbackClientWrapper from "@/app/ui/learn/feedbackClientWrapper";

export const metadata = {
    title: 'Learning Path Details',
};

interface LearningPathProps {
    learningpathid: string;
    userid: string;
}

export default async function Page( {params}: { params: {userid: string; learningpathid: string}}) {
    const { userid, learningpathid } = params;
    const learningPath = await fetchLearningPath(learningpathid);
    const learningStatistics = await fetchLearningStatistics(userid, learningpathid, learningPath.course.id.toString());
    const feedbacks: Feedback[] = await fetchFeedbacks(learningpathid);
    console.log("COURSE", learningPath.course);
    console.log("settings", learningPath.course.courseSettings);
    console.log("saats", learningStatistics);

    return (
        <main>
            <Breadcrumbs
                breadcrumbs={[
                    {label: 'Users', href: '/users'},
                    {label: 'Learning Paths', href: `/users/${userid}`},
                    {label: learningPath.id.toString(), href: `/users/${userid}/${learningpathid}`, active: true},
                ]}
            />
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
                <h1 className="text-2xl font-bold mb-6">{learningPath.course.subject}</h1>
                <p className="text-gray-700 mb-4">{learningPath.course.description}</p>
                <LearningProgress userId={userid} learningPathId={learningpathid} course={learningPath.course}
                                  learningStatistics={learningStatistics}/>
            </div>

            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
                <FeedbackClientWrapper
                    initialFeedbacks={feedbacks}
                    learningPathId={learningpathid}
                    professorId={userid}
                />
            </div>

                {/*<FeedbackForm*/}
                {/*    learningPathId={learningpathid}*/}
                {/*    professorId={userid}*/}
                {/*    onFeedbackSaved={() => {  }}*/}
                {/*/>*/}

                {/*<FeedbackList feedbacks={feedbacks} isStudentView={false} />*/}

                <RepeatedWrongAnswers userId={userid} learningPathId={learningPath.id.toString()}/>
                <Accordion type="single" collapsible>
                    {learningPath.learningSteps.map((step, index) => (
                        <AccordionItem key={step.id} value={`step-${index}`}>
                            <AccordionTrigger>{`Learning Step ${index + 1}`}</AccordionTrigger>
                            <AccordionContent>
                                <p className="mb-2"><strong>Reading Material:</strong> {step.readingMaterial}</p>
                                <p><strong>Explanation Text:</strong> {step.explanationText}</p>
                            </AccordionContent>
                        </AccordionItem>
                    ))}
                </Accordion>


        </main>
);
}
