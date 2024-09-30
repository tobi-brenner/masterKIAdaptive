import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import InitialAssessment from "@/app/ui/learn/initial-assessment";
import {Assessment, LearningPath} from "@/app/lib/types/courses";
import {fetchLearningPathData} from "@/app/lib/api/learningpath/learningpath";
import {SessionUser, User} from "@/app/lib/types/users";
import {fetchUserAttempts} from "@/app/lib/api/courses/learningpath";
import LearningStep from "@/app/ui/learn/learning-step";

interface LPAccordionProps {
    initialAssessment: Assessment;
    learningPath: LearningPath;
    user: SessionUser;
}

export const LearningPathAccordion = async ({initialAssessment, learningPath, user}: LPAccordionProps) => {
    // TODO: call userAttempts for initAssessment and learningSteps
    // TODO: For LearningSteps also get explanation texts

    const userAttemptData = await fetchUserAttempts(user.id);
    const sortedLearningSteps = [...learningPath.learningSteps].sort((a, b) => a.id! - b.id!);
    console.log("SKIPP", learningPath.initialAssessmentCompleted)
    return (
        <>
            <Accordion type={"multiple"}  className={"w-full"} defaultValue={["item-4"]}>
                <AccordionItem value="item-x">
                {/*    add course, topics/goals maybe extra progress ovcerview*/}
                </AccordionItem>
                <AccordionItem value="item-y" disabled={learningPath.initialAssessmentCompleted}>
                    <AccordionTrigger>Erstbewertung</AccordionTrigger>
                    <AccordionContent>
                        <InitialAssessment assessment={initialAssessment} attemptData={userAttemptData}
                        learningPath={learningPath} userId={user.id}/>
                    </AccordionContent>
                </AccordionItem>
                {sortedLearningSteps.map((step, i) => (
                    <AccordionItem key={i} value={`item-${i}`}>
                        <AccordionTrigger>Learning Step {i+1}</AccordionTrigger>
                        <AccordionContent>
                            <LearningStep learningStep={step} learningPath={learningPath} userId={user.id} />
                        </AccordionContent>
                    </AccordionItem>
                ))}
            </Accordion>
        </>
    )
}