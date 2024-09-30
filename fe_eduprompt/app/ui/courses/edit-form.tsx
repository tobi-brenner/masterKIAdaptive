'use client';
import {useEffect, useState} from 'react';
import {BE_BASE_URL} from '@/constants';
import Link from 'next/link';
import {Button} from '@/app/ui/button';
import {Assessment, BloomLevel, Course, LearningGoal, Task, Topic,} from '@/app/lib/types/courses';
import TopicsSection from '@/app/ui/courses/editCourse/TopicsSection';
import TaskSection from '@/app/ui/courses/editCourse/TaskSection';
import FileUploadSection from '@/app/ui/courses/editCourse/FileUploadSection';
import {fetchFromAPI} from '@/app/lib/utils/api';
import UploadedFileList from '@/app/ui/courses/editCourse/UploadedFileList';
import {useToast} from '@/components/ui/use-toast';
import {ToastAction} from '@/components/ui/toast';
import {Collapsible, CollapsibleContent, CollapsibleTrigger,} from '@/components/ui/collapsible';
import InitialAssessmentSection from '@/app/ui/courses/editCourse/InitialAssessmentSection';
import {updateCourse} from '@/app/ui/courses/actions';
import {createTask, deleteTask, updateTask} from '@/app/lib/api/courses/task';

interface GenerateTopicsResponse {
  topics: Topic[];
}

interface EditFormProps {
  profUserId: string;
  course: Course;
}

export default function EditForm({ profUserId, course }: EditFormProps) {
  const initialState = { message: null, errors: {} };
  // const [state, dispatch] = useFormState(updateCourse, initialState);
  const [loading, setLoading] = useState(false);
  const [newTopics, setNewTopics] = useState<Topic[]>([]);
  const { toast } = useToast();
  const [assessment, setAssessment] = useState<Assessment | null>(
    course.assessment,
  );

  const [isTopicsCollapsed, setIsTopicsCollapsed] = useState(false);
  const [isTasksCollapsed, setIsTasksCollapsed] = useState(false);
  const [isAssessmentCollapsed, setIsAssessmentCollapsed] = useState(true);

  const toggleTopicsCollapse = () => {
    setIsTopicsCollapsed(!isTopicsCollapsed);
  };
  const toggleTasksCollapse = () => {
    setIsTasksCollapsed(!isTasksCollapsed);
  };
  const toggleAssessmentCollapse = () => {
    setIsAssessmentCollapsed(!isAssessmentCollapsed);
  };

  const [formData, setFormData] = useState({
    subject: '',
    description: '',
    language: '',
    periodUnit: 'WEEKS',
    periodLength: 1,
    topics: [] as Topic[],
    files: [] as string[],
    deletedTopics: [] as number[],
    deletedLearningGoals: [] as number[],
  });



  useEffect(() => {
    if (course) {
      setFormData((prevFormData) => ({
        ...prevFormData,  // Keep existing form data
        subject: course.subject,
        description: course.description,
        language: course.courseSettings?.language || '',
        periodUnit: course.courseSettings?.periodUnit || 'WEEKS',
        periodLength: course.courseSettings?.periodLength || 1,
        // Only overwrite topics if course topics are different
        topics: prevFormData.topics.length === 0 ? course.topics : prevFormData.topics,
        files: course.courseMaterial?.files || [],
        deletedTopics: [],
        deletedLearningGoals: [],
      }));
      setAssessment(course.assessment || null);
    }
  }, [course]);



  const totalTasksCount = formData.topics.reduce(
      (acc, topic) => acc + (topic.tasks ? topic.tasks.length : 0),
      0
  );

  const handleChange = (
      e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
      index?: number,
      isNewTopic: boolean = false,
  ) => {
    const { name, value } = e.target;

    if (name.startsWith('topic-')) {
      if (isNewTopic) {
        const updatedNewTopics = [...newTopics];
        updatedNewTopics[index!] = {
          ...updatedNewTopics[index!],
          [name.split('-')[1]]: value,
        };
        setNewTopics(updatedNewTopics);
      } else {
        const updatedTopics = [...formData.topics];
        updatedTopics[index!] = {
          ...updatedTopics[index!],
          [name.split('-')[1]]: value,
        };
        setFormData({ ...formData, topics: updatedTopics });
      }
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };


  const handleGoalChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    goalIndex: number,
    topicIndex: number,
    isNewTopic: boolean = false,
  ) => {
    const { name, value } = e.target;
    const updatedTopics = isNewTopic ? [...newTopics] : [...formData.topics];
    const topic = updatedTopics[topicIndex];

    if (topic && topic.learningGoals) {
      const updatedGoals = topic.learningGoals.map((goal, index) =>
        index === goalIndex ? { ...goal, [name.split('-')[1]]: value } : goal,
      );
      updatedTopics[topicIndex] = { ...topic, learningGoals: updatedGoals };

      if (isNewTopic) {
        setNewTopics(updatedTopics);
      } else {
        setFormData({ ...formData, topics: updatedTopics });
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    console.log('formdata', formData);
    try {
      const updatedCourse: Course = await updateCourse(course.id, {
        subject: formData.subject,
        description: formData.description,
        language: formData.language,
        periodUnit: formData.periodUnit,
        periodLength: formData.periodLength,
        topics: [...formData.topics, ...newTopics],
        deletedTopics: formData.deletedTopics,
        deletedLearningGoals: formData.deletedLearningGoals,
      });
      console.log('Course updated successfully');

      // Set the response data in the frontend
      setFormData({
        subject: updatedCourse.subject,
        description: updatedCourse.description,
        language: updatedCourse.courseSettings?.language,
        periodUnit: updatedCourse.courseSettings?.periodUnit,
        periodLength: updatedCourse.courseSettings?.periodLength,
        topics: updatedCourse.topics,
        files: updatedCourse.courseMaterial?.files || [],
        deletedTopics: [],
        deletedLearningGoals: [],
      });

      toast({
        title: 'Course Updated',
        description: 'The course has been updated successfully.',
        action: <ToastAction altText="Dismiss">Dismiss</ToastAction>,
      });
    } catch (error) {
      console.error('Error updating course:', error);
      toast({
        title: 'Error',
        description: 'There was an error updating the course.',
        variant: 'destructive',
        // action: (
          // <ToastAction altText="Retry" onClick={handleSubmit}>
          //   Retry
          // </ToastAction>
        // ),
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUploadSuccess = (message: string) => {
    console.log(message);
    setFormData((prevState) => ({
      ...prevState,
      files: [...prevState.files, message],
    }));
  };

  const handleUploadError = (error: any) => {
    console.error(error);
    // Handle upload error
  };

  const generateTopics = async () => {
    setLoading(true);
    try {
      const keyword = formData.topics.length > 0 ? 'further' : 'create';
      const result = await fetchFromAPI<GenerateTopicsResponse>(
        `/learninggoal`,
        {
          method: 'POST',
          body: {
            courseId: course.id,
            keyword: keyword,
          },
        },
      );

      if (keyword === 'further') {
        setNewTopics(result.topics);

        console.log('NEW TOPICS', newTopics);
        console.log('NEW TOPICS', result.topics);
      } else {
        setFormData((prevState) => ({
          ...prevState,
          topics: result.topics,
        }));
        window.location.reload();
      }
    } catch (error) {
      console.error('Error generating topics:', error);
    } finally {
      setLoading(false);
    }
  };

  const createInitialAssessment = async () => {
    setLoading(true);
    try {
      await fetchFromAPI(`/initassessment/${course.id}`, {
        method: 'POST',
      });
      // Refetch course data
      const updatedCourse = await fetchFromAPI<Course>(`/courses/${course.id}`);
      console.log("UPDATED COURSE AFTER INIT ASSESS:::", updatedCourse)
      setAssessment(updatedCourse.assessment);
      toast({
        title: 'Success',
        description: 'Initial assessment created successfully',
        action: <ToastAction altText="Close">Close</ToastAction>,
      });
    } catch (error) {
      console.error('Error creating initial assessment:', error);
      toast({
        title: 'Error',
        description: 'Failed to create initial assessment',
        action: <ToastAction altText="Retry">Retry</ToastAction>,
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * handleGenerateTasks
   *
   * Asynchronously generates tasks for each topic, updates the course data
   *
   * The function triggers a loading state while it processes task generation for multiple topics
   * Upon completion, it fetches updated course data, updates the UI
   * with the new topics, and displays a success notification. In case of an error, it handles the error by displaying an
   * error notification with a retry option. The loading state is disabled whether the task generation succeeds or fails.
   *
   * @async
   * @function handleGenerateTasks
   *
   * @returns {Promise<void>} - This function does not return any value. It performs asynchronous tasks and updates the state/UI.
   *
   *
   * Error Handling:
   * If the task generation fails, an error message is shown using the `toast` notification system and the user is provided
   * with a 'Retry' button to try again.
   *
   * @throws {Error} Will catch and log any errors during the API call process.
   */

  const handleGenerateTasks = async () => {
    setLoading(true);
    try {
      const generateTasksPromises = formData.topics.map((topic) => {
        const payload = {
          courseId: course.id,
          topicId: topic.id,
          keyword: 'create',
        };

        return fetchFromAPI(`/tasks/generate`, {
          method: 'POST',
          body: payload,
        });
      });

      // Wait for all the tasks to be generated
      await Promise.all(generateTasksPromises);

      // Fetch the updated course data or handle it as needed
      const updatedCourse = await fetchFromAPI<Course>(`/courses/${course.id}`);
      setFormData({
        ...formData,
        topics: updatedCourse.topics,
      });
      setIsTasksCollapsed(true);

      toast({
        title: 'Success',
        description: 'Tasks generated successfully',
        action: <ToastAction altText="Close">Close</ToastAction>,
      });
    } catch (error) {
      console.error('Error generating tasks:', error);
      toast({
        title: 'Error',
        description: 'Failed to generate tasks',
        variant: 'destructive',
        action: (
            <ToastAction altText="Retry" onClick={handleGenerateTasks}>
              Retry
            </ToastAction>
        ),
      });
    } finally {
      setLoading(false);
    }
  };

  const generateTasksForTopic = async (topicIndex: number, keyword: string) => {
    setLoading(true);
    try {
      const topic = formData.topics[topicIndex];
      const payload = {
        courseId: course.id,
        topicId: topic.id,
        keyword: keyword,
      };
      console.log("API BODY:", payload);

      const result = await fetchFromAPI<Task[]>(`/tasks/generate`, {
        method: 'POST',
        body: payload,
      });
      console.log("API Result:", result);
      const updatedCourse = await fetchFromAPI<Course>(`/courses/${course.id}`);
      console.log("GEN TASK UPDATECOURSE", updatedCourse);
      setFormData(prevFormData => ({ ...prevFormData, topics: updatedCourse.topics }));

      toast({
        title: 'Success',
        description: `Tasks generated successfully`,
        action: <ToastAction altText="Close">Close</ToastAction>,
      });
    } catch (error) {
      console.error('Error generating tasks:', error);
      toast({
        title: 'Error',
        description: 'Failed to generate tasks',
        variant: 'destructive',
        action: (
            <ToastAction
                altText="Retry"
                onClick={() => generateTasksForTopic(topicIndex, keyword)}
            >
              Retry
            </ToastAction>
        ),
      });
    } finally {
      setLoading(false);
    }
  };

  const handleTaskChange = (
      e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement >,
      topicIndex: number,
      taskIndex: number,
  ) => {
    const { name, value } = e.target;
    const updatedTopics = [...formData.topics];
    const task = updatedTopics[topicIndex].tasks![taskIndex];

    updatedTopics[topicIndex].tasks![taskIndex] = {
      ...task,
      [name]: value,
    };

    // setFormData({ ...formData, topics: updatedTopics });
    setFormData(prevFormData => ({ ...prevFormData, topics: updatedTopics }));
  };


  const addTask = (topicIndex: number) => {
    console.log('addTask called for topicIndex:', topicIndex);
    const newTask: Task = {
      question: '',
      correctAnswer: '{}',
      bloomLevel: '',
      taskType: '',
      options: {},
    };

    // Update the state with a callback to avoid stale closures
    setFormData((prevFormData) => {
      const updatedTopics = [...prevFormData.topics];
      updatedTopics[topicIndex].tasks = [
        ...(updatedTopics[topicIndex].tasks || []),
        newTask,
      ];

      return {
        ...prevFormData,
        topics: updatedTopics,
      };
    });
  };



    const deleteTask = (topicIndex: number, taskIndex: number) => {
      const updatedTopics = [...formData.topics];
      updatedTopics[topicIndex].tasks!.splice(taskIndex, 1);

      setFormData({ ...formData, topics: updatedTopics });
    };
  const updateTaskInAssessment = async (taskId: number, assessmentId: number) => {
    try {
      await fetchFromAPI(`/assessment/add-task/${taskId}/${assessmentId}`, { method: 'POST' });
      toast({
        title: 'Task added',
        description: 'Task successfully added to the assessment.',
      });
    } catch (error) {
      console.error('Error updating task:', error);
      toast({
        title: 'Error',
        description: 'There was an error adding the task to the assessment.',
      });
    }
  };
  const removeTaskFromAssessment = async (taskId: number) => {
    try {
      await fetchFromAPI(`/assessment/remove-task/${taskId}`, { method: 'POST' });
      toast({
        title: 'Task removed',
        description: 'Task successfully removed from the assessment.',
      });
    } catch (error) {
      console.error('Error removing task:', error);
      toast({
        title: 'Error',
        description: 'There was an error removing the task from the assessment.',
      });
    }
  };


  const tasksExist = formData.topics.some(
      (topic) => topic.tasks && topic.tasks.length > 0,
  );


  /** -----------------------------------------------------------------------------------
   * TOPIC AND LEARNINGGOAL CRUD
   -------------------------------------------------------------------------------------*/

  // Save or Update a Topic
  const saveTopic = async (topic: Topic, isNewTopic: boolean, topicIndex?: number) => {
        setLoading(true);
        try {
          let response: any;
          const payload = {
            name: topic.name,
            description: topic.description,
            orderNumber: topic.orderNumber !== undefined ? topic.orderNumber! : 1,
            maxBloom: topic.maxBloom,
            courseId: isNewTopic ? course.id : undefined,
          };

          const url = `${BE_BASE_URL}/topics${isNewTopic ? '' : `/${topic.id}`}`;
          const method = isNewTopic ? 'POST' : 'PUT';

          response = await fetch(url, {
            method: method,
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
          });

          if (!response.ok) {
            const errorMessage = await response.text();  // Get the response text in case of an error
            console.error('Error response from backend:', errorMessage);
            throw new Error(errorMessage);
          }

          let updatedTopic: any;
          if (response.status !== 204) {
            updatedTopic = await response.json();
          }

          if (isNewTopic) {
            // Update the existing topic in the newTopics array
            setNewTopics((prevTopics) => {
              const updatedNewTopics = [...prevTopics];
              updatedNewTopics[topicIndex!] = updatedTopic;
              return updatedNewTopics;
            });
          } else {
            // Update the existing topic in the topics array
            setFormData((prevFormData) => {
              const updatedTopics = [...prevFormData.topics];
              if (topicIndex !== undefined) {
                updatedTopics[topicIndex] = updatedTopic;
              }
              return { ...prevFormData, topics: updatedTopics };
            });
          }

          toast({
            title: isNewTopic ? 'Topic Created' : 'Topic Updated',
            description: `The topic has been ${isNewTopic ? 'created' : 'updated'} successfully.`,
            action: <ToastAction altText="Dismiss">Dismiss</ToastAction>,
          });
        } catch (error) {
          console.error('Error saving topic:', error);
          toast({
            title: 'Error',
            description: `There was an error ${isNewTopic ? 'creating' : 'updating'} the topic.`,
            variant: 'destructive',
            action: <ToastAction altText="Retry">Retry</ToastAction>,
          });
        } finally {
          setLoading(false);
        }
      };

  const handleOptionChange = (
      optionKey: string,
      topicIndex: number,
      taskIndex: number,
      isChecked: boolean
  ) => {
    setFormData((prevData) => {
      const updatedTopics = [...prevData.topics];
      const updatedTasks = [...updatedTopics[topicIndex].tasks!];
      const task = { ...updatedTasks[taskIndex] };

      let selectedOptions: Record<string, boolean>;

      // Correct the formatting and safely parse JSON
      try {
        // Assuming correctAnswer is a JSON string or null
        selectedOptions = task.correctAnswer ? JSON.parse(task.correctAnswer) : {};
      } catch (error) {
        console.error("Failed to parse correctAnswer JSON:", error);
        selectedOptions = {};
      }

      // Update the selected options based on user interaction
      if (isChecked) {
        selectedOptions[optionKey] = true;
      } else {
        delete selectedOptions[optionKey];
      }

      // Serialize the updated options back to JSON
      task.correctAnswer = JSON.stringify(selectedOptions);
      updatedTasks[taskIndex] = task;
      updatedTopics[topicIndex].tasks = updatedTasks;

      return { ...prevData, topics: updatedTopics };
    });
  };


// Delete a Topic
  const deleteTopic = async (
      topicIndex: number,
      isNewTopic: boolean
  ) => {
    try {
      if (isNewTopic) {
        setNewTopics((prevState) => prevState.filter((_, i) => i !== topicIndex));
      } else {
        const topicToDelete = formData.topics[topicIndex];
        await fetchFromAPI(`/topics/${topicToDelete.id}`, { method: 'DELETE' });
        setFormData((prevState) => ({
          ...prevState,
          topics: prevState.topics.filter((_, i) => i !== topicIndex),
        }));
        toast({ title: 'Topic deleted', description: 'Topic has been deleted.' });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'There was an error deleting the topic.',
        variant: 'destructive',
      });
    }
  };

// Add a new Topic
  const addTopic = () => {
    // Find the current highest order number
    const highestOrderNumber = formData.topics.reduce((max, topic) => Math.max(max, topic.orderNumber!), 0);

    // Create a new topic with the orderNumber incremented by 1
    const newTopic: Topic = {
      id: null,  // This will be assigned after saving to the backend
      name: '',
      description: '',
      orderNumber: highestOrderNumber + 1,
      maxBloom: BloomLevel.NONE,
      learningGoals: [],
      tasks: [],
    };

    // Add the new topic to the state
    setNewTopics((prevState) => {
      const updatedTopics = [...prevState, newTopic];

      // Scroll to the new topic after it's added
      setTimeout(() => {
        const lastTopicElement = document.getElementById(`new-topic-${updatedTopics.length - 1}`);
        lastTopicElement?.scrollIntoView({ behavior: 'smooth' });
      }, 100); // Adjust timing as necessary

      return updatedTopics;
    });
  };



// Save or Update a Learning Goal
  const saveLearningGoal = async (
      learningGoal: LearningGoal,
      topicIndex: number,
      goalIndex: number,
      isNewTopic: boolean
  ) => {
    const topic = isNewTopic ? newTopics[topicIndex] : formData.topics[topicIndex];
    try {
      if (learningGoal.id) {
        const updatedGoal = await fetchFromAPI<LearningGoal>(
            `/learninggoals/${learningGoal.id}`,
            {
              method: 'PUT',
              body: learningGoal,
            }
        );
        const updatedGoals = topic.learningGoals.map((goal, i) =>
            i === goalIndex ? updatedGoal : goal
        );
        updateTopicGoals(updatedGoals, topicIndex, isNewTopic);
        toast({
          title: 'Learning Goal updated',
          description: 'Learning goal has been updated.',
        });
      } else {
        const savedGoal = await fetchFromAPI<LearningGoal>(`/learninggoals`, {
          method: 'POST',
          body: { ...learningGoal, topicId: topic.id },
        });
        const updatedGoals = [...topic.learningGoals, savedGoal];
        updateTopicGoals(updatedGoals, topicIndex, isNewTopic);
        toast({
          title: 'Learning Goal saved',
          description: 'New learning goal has been saved.',
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'There was an error saving the learning goal.',
        variant: 'destructive',
      });
    }
  };

  // Delete a Learning Goal
  const deleteLearningGoal = async (topicIndex: number, goalIndex: number, isNewTopic: boolean = false) => {
    setLoading(true);
    try {
      const topic = isNewTopic ? newTopics[topicIndex] : formData.topics[topicIndex];
      const goalToDelete = topic.learningGoals[goalIndex];

      if (!isNewTopic && goalToDelete.id) {
        await fetchFromAPI(`/learninggoals/${goalToDelete.id}`, {
          method: 'DELETE',
        });

        // Update the frontend state after deletion
        const updatedTopics = [...formData.topics];
        updatedTopics[topicIndex].learningGoals.splice(goalIndex, 1);
        setFormData({ ...formData, topics: updatedTopics });
      } else if (isNewTopic) {
        const updatedNewTopics = [...newTopics];
        updatedNewTopics[topicIndex].learningGoals.splice(goalIndex, 1);
        setNewTopics(updatedNewTopics);
      }

      toast({
        title: 'Success',
        description: 'Learning goal deleted successfully',
        action: <ToastAction altText="Close">Close</ToastAction>,
      });
    } catch (error) {
      console.error('Error deleting learning goal:', error);
      toast({
        title: 'Error',
        description: 'Failed to delete learning goal',
        variant: 'destructive',
        action: (
            <ToastAction altText="Retry" onClick={() => deleteLearningGoal(topicIndex, goalIndex, isNewTopic)}>
              Retry
            </ToastAction>
        ),
      });
    } finally {
      setLoading(false);
    }
  };

// Add a new Learning Goal to a Topic
  const addLearningGoal = (topicIndex: number, isNewTopic: boolean) => {
    const newGoal: LearningGoal = {
      id: null,
      goal: '',
      description: '',
      maxBloom: ''
    };

    if (isNewTopic) {
      setNewTopics((prevState) => {
        const updatedNewTopics = [...prevState];
        updatedNewTopics[topicIndex].learningGoals.push(newGoal);
        return updatedNewTopics;
      });
    } else {
      setFormData((prevState) => {
        const updatedTopics = [...prevState.topics];
        updatedTopics[topicIndex].learningGoals.push(newGoal);
        return { ...prevState, topics: updatedTopics };
      });
    }
  };

// Helper function to update Learning Goals within a Topic
  const updateTopicGoals = (
      updatedGoals: LearningGoal[],
      topicIndex: number,
      isNewTopic: boolean
  ) => {
    if (isNewTopic) {
      setNewTopics((prevState) => {
        const updatedNewTopics = [...prevState];
        updatedNewTopics[topicIndex].learningGoals = updatedGoals;
        return updatedNewTopics;
      });
    } else {
      setFormData((prevState) => {
        const updatedTopics = [...prevState.topics];
        updatedTopics[topicIndex].learningGoals = updatedGoals;
        return { ...prevState, topics: updatedTopics };
      });
    }
  };



  return (
      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Course Information Section */}
        <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm">
          <h2 className="mb-4 text-xl font-semibold text-gray-800">Kursinformationen</h2>
          <div className="mb-4 hidden">
            <input
                type="text"
                id="profUserId"
                name="profUserId"
                value={profUserId}
                readOnly
            />
          </div>
          <div className="mb-4">
            <label htmlFor="subject" className="mb-2 block text-sm font-medium text-gray-600">
              Kursname
            </label>
            <input
                id="subject"
                name="subject"
                type="text"
                value={formData.subject}
                onChange={handleChange}
                placeholder="Enter subject of course"
                className="block w-full rounded-md border border-gray-300 p-2"
            />
          </div>
          <div className="mb-4">
            <label htmlFor="description" className="mb-2 block text-sm font-medium text-gray-600">
              Beschreibung
            </label>
            <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Enter a description of course"
                className="block w-full rounded-md border border-gray-300 p-2"
            />
          </div>
          <div className="mb-4">
            <label htmlFor="language" className="mb-2 block text-sm font-medium text-gray-600">
              Sprache
            </label>
            <input
                id="language"
                name="language"
                type="text"
                value={formData.language}
                onChange={handleChange}
                placeholder="Enter language of course"
                className="block w-full rounded-md border border-gray-300 p-2"
            />
          </div>

          {/* Period Fields */}
          <div className="mb-4">
            <label htmlFor="periodLength" className="mb-2 block text-sm font-medium text-gray-600">
              Dauer
            </label>
            <div className="flex space-x-4">
              <input
                  id="periodLength"
                  name="periodLength"
                  type="number"
                  value={formData.periodLength}
                  onChange={handleChange}
                  placeholder="Enter duration"
                  className="block w-full rounded-md border border-gray-300 p-2"
              />
              <select
                  id="periodUnit"
                  name="periodUnit"
                  value={formData.periodUnit}
                  onChange={handleChange}
                  className="block w-full rounded-md border border-gray-300 p-2"
              >
                <option value="WEEKS">Wochen</option>
                <option value="MONTHS">Monate</option>
              </select>
            </div>
          </div>
          <Button type="submit">Änderungen speichern</Button>
        </div>

        {/* File Upload Section */}
        <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm">
          <h1 className="mb-4 text-xl text-gray-800 font-bold">Kursmaterialien</h1>
          <UploadedFileList files={formData.files}/>
          <FileUploadSection
              onUploadSuccess={handleUploadSuccess}
              onUploadError={() => "Error!"}
              courseId={course.id}
          />
          {formData.files.length > 0 && (
              <Button type="button" onClick={generateTopics} disabled={loading} className="mt-4">
                {loading
                    ? 'Generating...'
                    : formData.topics.length > 0
                        ? 'Weitere Themenbereiche generieren'
                        : 'Themenbereiche generieren'}
              </Button>
          )}
        </div>
        {/*Topic section*/}
        <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm">
          <div
              className="flex items-center justify-between cursor-pointer"
              onClick={toggleTopicsCollapse}
              aria-expanded={!isTopicsCollapsed}
              aria-controls="topics-content"
          >
            <h2 className="text-xl font-semibold text-gray-800">
              Themenbereiche ({formData.topics.length})
            </h2>
            <button
                type="button"
                className="p-2 text-gray-600 hover:text-gray-800"
                aria-label={isTopicsCollapsed ? 'Expand Topics' : 'Collapse Topics'}
            >
              {isTopicsCollapsed ? '↓' : '↑'}
            </button>
          </div>

          <Collapsible open={isTopicsCollapsed}>
            <CollapsibleContent id="topics-content">
              <TopicsSection
                  topics={formData.topics}
                  newTopics={newTopics}
                  handleChange={handleChange}
                  handleGoalChange={handleGoalChange}
                  saveTopic={saveTopic}
                  addLearningGoal={addLearningGoal}
                  addTopic={addTopic}
                  deleteLearningGoal={deleteLearningGoal}
                  deleteTopic={deleteTopic}
                  saveLearningGoal={saveLearningGoal}
              />
            </CollapsibleContent>
          </Collapsible>
          {/*<Button type="submit">Save changes</Button>*/}
        </div>

        {/* Tasks Section */}
        <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm">
          <div
              className="flex items-center justify-between cursor-pointer"
              onClick={toggleTasksCollapse}
              aria-expanded={!isTasksCollapsed}
              aria-controls="tasks-content"
          >
            <h2 className="text-xl font-semibold text-gray-800">Tasks ({totalTasksCount})</h2>
            <button
                type="button"
                className="p-2 text-gray-600 hover:text-gray-800"
                aria-label={isTasksCollapsed ? 'Expand Tasks' : 'Collapse Tasks'}
            >
              {isTasksCollapsed ? '↓' : '↑'}
            </button>
          </div>

          <Collapsible open={isTasksCollapsed}>
            <CollapsibleContent id="tasks-content">
              {tasksExist ? (
                  <TaskSection
                      topics={formData.topics}
                      handleTaskChange={handleTaskChange}
                      addTask={addTask}
                      generateTasksForTopic={generateTasksForTopic}
                      deleteTask={deleteTask}
                      handleOptionChange={handleOptionChange}
                  />
              ) : (
                  <Button
                      onClick={handleGenerateTasks}
                      disabled={loading}
                      className="w-full text-lg font-semibold text-white-50 bg-bg-blue-500 py-2 rounded"
                  >
                    {loading ? 'Generating Tasks...' : 'Generate Tasks'}
                  </Button>
              )}
            </CollapsibleContent>
          </Collapsible>
        </div>

        {/* Initial Assessment Section */}
        <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm">
          <div
              className="flex items-center justify-between cursor-pointer"
              onClick={toggleAssessmentCollapse}
              aria-expanded={!isAssessmentCollapsed}
              aria-controls="assessment-content"
          >
            <h2 className="text-xl font-semibold text-gray-800">Erstbewertung</h2>
            <button
                type="button"
                className="p-2 text-gray-600 hover:text-gray-800"
                aria-label={isAssessmentCollapsed ? 'Expand Initial Assessment' : 'Collapse Initial Assessment'}
            >
              {isAssessmentCollapsed ? '↓' : '↑'}
            </button>
          </div>

          <Collapsible open={!isAssessmentCollapsed}>
            <CollapsibleContent id="assessment-content">
              {assessment ? (
                  <InitialAssessmentSection
                      assessment={assessment}
                      createInitialAssessment={createInitialAssessment}
                      removeTaskFromAssessment={removeTaskFromAssessment}
                      updateTaskInAssessment={updateTaskInAssessment}
                      topics={course.topics}

                  />
              ) : (
                  <Button
                      type="button"
                      onClick={createInitialAssessment}
                      disabled={loading}
                      className="mt-4"
                  >
                    {loading ? 'Creating...' : 'Create Initial Assessment'}
                  </Button>
              )}
            </CollapsibleContent>
          </Collapsible>
        </div>


        {/* Form Actions */}
        <div className="flex justify-end gap-4">
          <Link
              href={`/dashboard/courses/${course.id}`}
              className="flex h-10 items-center rounded-lg bg-gray-100 px-4 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-200"
          >
            Cancel
          </Link>
          <Button type="submit">Save changes</Button>
        </div>
      </form>

  )
      ;
}
