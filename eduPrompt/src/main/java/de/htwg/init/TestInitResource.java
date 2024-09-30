package de.htwg.init;

import de.htwg.course.model.*;
import de.htwg.course.model.tasktype.FreeTextTask;
import de.htwg.course.model.tasktype.MultipleChoiceTask;
import de.htwg.course.model.tasktype.ShortAnswerTask;
import de.htwg.course.model.tasktype.TrueFalseTask;
import de.htwg.learningpath.model.*;
import de.htwg.user.model.User;
import de.htwg.user.service.UserService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Path("/test-init")
public class TestInitResource {

    @Inject
    UserService userService;

    private static final Logger LOG = Logger.getLogger(TestInitResource.class);

    @POST
    @Transactional
    public Response initTestData() {
        try {
            // Create Users
            User user1 = new User();
            user1.email = "user@nextmail.com";
            user1.firstName = "User";
            user1.lastName = "User";
            user1.password = "123456";
            user1.studentNumber = "123456";
            user1.role = "student";
            user1.preferredLanguage = "english";
            user1.persist();

            User user2 = new User();
            user2.email = "tobiasbrenner2@gmail.com";
            user2.firstName = "Tobi";
            user2.lastName = "Brenner";
            user2.password = "admin123";
            user2.studentNumber = "12345";
            user2.role = "teacher";
            user2.preferredLanguage = "deutsch";
            user2.persist();

            User user3 = new User();
            user3.email = "tobiasbrenner23@gmail.com";
            user3.firstName = "Prof";
            user3.lastName = "Prof";
            user3.password = "hashed_password_here";
            user3.studentNumber = "123455";
            user3.role = "teacher";
            user3.preferredLanguage = "english";
            user3.persist();

            // Create Course
            Course course = new Course();
            course.description = "git basics";
            course.subject = "git";
            course.prof = user2;
            course.users.add(user1);
            course.users.add(user2);
            course.users.add(user3);
            course.persist();


            Course course1 = new Course();
            course1.description = "IT security is the protection of computer systems and networks from threats that may result in unauthorized information disclosure, theft of (or damage to) hardware, software, or data, as well as from the disruption or misdirection of the services they provide.";
            course1.subject = "IT Security";
            course1.prof = user2;
            course1.persist();

            CourseSettings courseSettings = new CourseSettings();
            courseSettings.language = "en";
            courseSettings.periodUnit = ChronoUnit.WEEKS;
            courseSettings.periodLength = 2;
            courseSettings.course = course;
            courseSettings.persist();

            CourseSettings courseSettings1 = new CourseSettings();
            courseSettings1.language = "de";
            courseSettings1.periodUnit = ChronoUnit.WEEKS;
            courseSettings1.periodLength = 3;
            courseSettings1.course = course1;
            courseSettings1.persist();

            // Create Course Material
            CourseMaterial courseMaterial = new CourseMaterial();
            courseMaterial.course = course;
            courseMaterial.files.add("Git_tutorial_c0835d12-8eb2-4fbe-bbd4-28f61639cf73.pdf");
            courseMaterial.persist();

            CourseMaterial courseMaterial1 = new CourseMaterial();
            courseMaterial1.course = course1;
            courseMaterial1.files.add("HTWG-MSI-ITSEC-08 - Anonymity in Networks.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SECMGT Exercise sheet 01 - Security Policy");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SECMGT Exercise sheet 02 - Backup Concept.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SECMGT Exercise sheet 03 - Awareness Metrics.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-01 - History and Current Incidents.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-02a - Authentication - Local user authentication.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-02b - Authentication - Tokens and biometrics.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-03a - Access Control Models.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-03b - Federated Identity Management.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-04a - Access Control Policies.pdf");
            courseMaterial1.files.add("HTWG-MSI-ITSEC-SOS-04b - Access Control Mechanisms.pdf");
            courseMaterial1.persist();

            // Create Assessment
            Assessment assessment = new Assessment();
            assessment.course = course;
            assessment.description = "Initial assessment based on AI recommendations";
            assessment.isInitial = true;
            assessment.persist();
            course.assessment = assessment;
            course.persist();


            Assessment assessment1 = new Assessment();
            assessment1.course = course1;
            assessment1.description = "Initial assessment for IT-Security course";
            assessment1.isInitial = true;
            assessment1.persist();
            course1.assessment = assessment1;
            course1.persist();

            userService.enrollUserToCourse(user1.id, course1.id);
            userService.enrollUserToCourse(user2.id, course1.id);
            userService.enrollUserToCourse(user3.id, course1.id);
            // Create Topics
            Topic topic1 = new Topic();
            topic1.course = course;
            topic1.description = "Understanding the basics of Git and version control";
            topic1.name = "Introduction to Git";
            topic1.orderNumber = 1;
            topic1.maxBloom = BloomLevel.APPLYING;
            topic1.persist();
            course.topics.add(topic1);

            Topic topic2 = new Topic();
            topic2.course = course;
            topic2.description = "Learning about committing changes, branching, and merging in Git";
            topic2.name = "Committing and Branching";
            topic2.orderNumber = 2;
            topic2.maxBloom = BloomLevel.ANALYZING;
            topic2.persist();
            course.topics.add(topic2);

            Topic topic3 = new Topic();
            topic3.course = course;
            topic3.description = "Exploring advanced features and best practices in Git";
            topic3.name = "Advanced Git Features";
            topic3.orderNumber = 3;
            topic3.maxBloom = BloomLevel.CREATING;
            topic3.persist();
            course.topics.add(topic3);
            course.persist();

            Topic topic4 = new Topic();
            topic4.course = course1;
            topic4.description = "Understanding the basic concepts and principles of IT security, including goals, historical approaches, and the relationship between security and adversary.";
            topic4.name = "Fundamentals of IT Security";
            topic4.orderNumber = 1;
            topic4.maxBloom = BloomLevel.APPLYING;
            topic4.persist();

            Topic topic5 = new Topic();
            topic5.course = course1;
            topic5.description = "Exploring various cyber threats, including botnets, spyware, malware, and computer crimes, as well as understanding the security implications of mobile device operating systems and ad-hoc networks.";
            topic5.name = "Cyber Threats and Attacks";
            topic5.orderNumber = 2;
            topic5.maxBloom = BloomLevel.APPLYING;
            topic5.persist();

            Topic topic6 = new Topic();
            topic6.course = course1;
            topic6.description = "Understanding the security aspects of data, applications, and network infrastructure, including topics such as data protection, legal issues, and network security.";
            topic6.name = "Data and Application Security";
            topic6.orderNumber = 3;
            topic6.maxBloom = BloomLevel.ANALYZING;
            topic6.persist();

            Topic topic7 = new Topic();
            topic7.course = course1;
            topic7.description = "Understanding the role-based access control and modeling high-level roles in an organization, as well as exploring the importance of security awareness and training in information security.";
            topic7.name = "Roles and Security Management";
            topic7.orderNumber = 4;
            topic7.maxBloom = BloomLevel.EVALUATING;
            topic7.persist();

            Topic topic8 = new Topic();
            topic8.course = course1;
            topic8.description = "Understanding the development and implementation of security policies, as well as exploring the requirements and standards for information security management.";
            topic8.name = "Security Policy and Management";
            topic8.orderNumber = 5;
            topic8.maxBloom = BloomLevel.CREATING;
            topic8.persist();


            // Create Tasks
            MultipleChoiceTask task1 = new MultipleChoiceTask();
            task1.assessment = assessment;
            task1.bloomLevel = BloomLevel.REMEMBERING;
            task1.correctAnswer = "{\"option1\":\"To manage different versions of the software\"}";
            task1.question = "What is the purpose of version control in software development?";
            task1.topic = topic1;
            task1.verified = false;
            Map<String, String> options1 = new HashMap<>();
            options1.put("option1", "To manage different versions of the software");
            options1.put("option2", "To track changes in the code");
            options1.put("option3", "To collaborate with team members on code");
            task1.setOptions(options1);
            task1.persist();

            FreeTextTask task2 = new FreeTextTask();
            task2.assessment = assessment;
            task2.bloomLevel = BloomLevel.UNDERSTANDING;
            task2.correctAnswer = "{\"text\":\"Git allows for easy collaboration, efficient branching and merging, and provides a complete history of changes made to the code.\"}";
            task2.question = "Explain the benefits of using Git as a version control system.";
            task2.topic = topic1;
            task2.verified = false;
            task2.persist();

            ShortAnswerTask task3 = new ShortAnswerTask();
            task3.assessment = assessment;
            task3.bloomLevel = BloomLevel.APPLYING;
            task3.correctAnswer = "{\"text\":\"git init\"}";
            task3.question = "Provide the command to initialize a new local repository in Git.";
            task3.topic = topic1;
            task3.verified = false;
            task3.persist();

            TrueFalseTask task4 = new TrueFalseTask();
            task4.assessment = assessment;
            task4.bloomLevel = BloomLevel.UNDERSTANDING;
            task4.correctAnswer = "{\"value\":\"true\"}";
            task4.question = "Commits in Git are identified by a unique hexadecimal number.";
            task4.topic = topic2;
            task4.verified = false;
            task4.persist();

            FreeTextTask task5 = new FreeTextTask();
            task5.assessment = assessment;
            task5.bloomLevel = BloomLevel.APPLYING;
            task5.correctAnswer = "{\"text\":\"Good commit messages provide clear and descriptive information about the changes made, making it easier to understand the history of the codebase.\"}";
            task5.question = "Explain the significance of good commit messages in Git.";
            task5.topic = topic2;
            task5.verified = false;
            task5.persist();

            MultipleChoiceTask task6 = new MultipleChoiceTask();
            task6.assessment = assessment;
            task6.bloomLevel = BloomLevel.CREATING;
            task6.correctAnswer = "{\"option1\":\"Staging changes and then committing\"}";
            task6.question = "What are the two steps involved in committing changes in Git?";
            task6.topic = topic2;
            task6.verified = false;
            Map<String, String> options6 = new HashMap<>();
            options6.put("option1", "Staging changes and then committing");
            options6.put("option2", "Modifying files and then committing");
            options6.put("option3", "Creating a new branch and then committing");
            task6.setOptions(options6);
            task6.persist();

            TrueFalseTask task7 = new TrueFalseTask();
            task7.assessment = assessment;
            task7.bloomLevel = BloomLevel.REMEMBERING;
            task7.correctAnswer = "{\"value\":\"true\"}";
            task7.question = "Git allows for easy cloning of existing repositories from remote locations.";
            task7.topic = topic3;
            task7.verified = false;
            task7.persist();

            FreeTextTask task8 = new FreeTextTask();
            task8.assessment = assessment;
            task8.bloomLevel = BloomLevel.APPLYING;
            task8.correctAnswer = "{\"text\":\"The 'git pull' command is used to fetch and download content from a remote repository and immediately update the local repository to match that content.\"}";
            task8.question = "Explain the purpose of the command 'git pull' in Git.";
            task8.topic = topic3;
            task8.verified = false;
            task8.persist();

            ShortAnswerTask task9 = new ShortAnswerTask();
            task9.assessment = assessment;
            task9.bloomLevel = BloomLevel.EVALUATING;
            task9.correctAnswer = "{\"text\":\"Common issues in Git include merge conflicts and accidental commits. These can be avoided by practicing good branching strategies and using descriptive commit messages.\"}";
            task9.question = "What are some common issues in Git and how can they be avoided?";
            task9.topic = topic3;
            task9.verified = false;
            task9.persist();

            // Create Learning Goals
            LearningGoal goal1 = new LearningGoal();
            goal1.description = "Learn the importance and benefits of version control in software development";
            goal1.goal = "Understanding Version Control";
            goal1.topic = topic1;
            goal1.maxBloom = BloomLevel.UNDERSTANDING;
            goal1.persist();

            LearningGoal goal2 = new LearningGoal();
            goal2.description = "Understand the purpose and functionality of Git as a version control system";
            goal2.goal = "Introduction to Git";
            goal2.topic = topic1;
            goal2.maxBloom = BloomLevel.UNDERSTANDING;
            goal2.persist();

            LearningGoal goal3 = new LearningGoal();
            goal3.description = "Learn how to initialize a new local repository and use basic Git commands";
            goal3.goal = "Setting Up a Local Repository";
            goal3.topic = topic1;
            goal3.maxBloom = BloomLevel.APPLYING;
            goal3.persist();

            LearningGoal goal4 = new LearningGoal();
            goal4.description = "Understand the significance of good commit messages and how to commit changes in Git";
            goal4.goal = "Committing Changes";
            goal4.topic = topic2;
            goal4.maxBloom = BloomLevel.APPLYING;
            goal4.persist();

            LearningGoal goal5 = new LearningGoal();
            goal5.description = "Learn how to create, switch, and merge branches in Git for collaborative development";
            goal5.goal = "Working with Branches";
            goal5.topic = topic2;
            goal5.maxBloom = BloomLevel.EVALUATING;
            goal5.persist();

            LearningGoal goal6 = new LearningGoal();
            goal6.description = "Understand the concept of remote repositories and how to work with them in Git";
            goal6.goal = "Remote Repositories";
            goal6.topic = topic2;
            goal6.maxBloom = BloomLevel.EVALUATING;
            goal6.persist();

            LearningGoal goal7 = new LearningGoal();
            goal7.description = "Learn how to monitor changes, view commit history, and use logging in Git";
            goal7.goal = "Monitoring and Logging";
            goal7.topic = topic3;
            goal7.maxBloom = BloomLevel.EVALUATING;
            goal7.persist();

            LearningGoal goal8 = new LearningGoal();
            goal8.description = "Understand how to configure Git settings and manage user information";
            goal8.goal = "Git Configuration";
            goal8.topic = topic3;
            goal8.maxBloom = BloomLevel.EVALUATING;
            goal8.persist();

            LearningGoal goal9 = new LearningGoal();
            goal9.description = "Explore common issues and best practices in Git to improve workflow and avoid pitfalls";
            goal9.goal = "Best Practices and Common Issues";
            goal9.topic = topic3;
            goal9.maxBloom = BloomLevel.EVALUATING;
            goal9.persist();

            // Create Learning Goals for Topic 4 (Fundamentals of IT Security)

            LearningGoal goal10 = new LearningGoal();
            goal10.description = "Learn about the goals of IT security and the concepts of confidentiality, integrity, and availability (CIA).";
            goal10.goal = "Understanding the Goals of IT Security";
            goal10.topic = topic4;
            goal10.maxBloom = BloomLevel.EVALUATING;
            goal10.persist();

            LearningGoal goal11 = new LearningGoal();
            goal11.description = "Understand the historical approach of provable security and the current approaches of partitioning, detecting, and repairing security issues.";
            goal11.goal = "Exploring Historical and Current Approaches to IT Security";
            goal11.topic = topic4;
            goal11.persist();

            LearningGoal goal12 = new LearningGoal();
            goal12.description = "Differentiate between security and safety in the context of IT systems.";
            goal12.goal = "Differentiating Security and Safety";
            goal12.topic = topic4;
            goal12.persist();

            LearningGoal goal13 = new LearningGoal();
            goal13.description = "Explore the concept of security in relation to potential adversaries and the continuous race between attackers and defenders.";
            goal13.goal = "Understanding Security Relative to Adversary";
            goal13.topic = topic4;
            goal13.persist();

// Create Learning Goals for Topic 5 (Cyber Threats and Attacks)

            LearningGoal goal14 = new LearningGoal();
            goal14.description = "Learn about botnets, spyware, malware analysis, and computer crimes such as phishing.";
            goal14.goal = "Understanding Cyber Threats and Attacks";
            goal14.topic = topic5;
            goal14.maxBloom = BloomLevel.REMEMBERING;
            goal14.persist();

            LearningGoal goal15 = new LearningGoal();
            goal15.description = "Understand the security challenges and considerations specific to mobile device operating systems.";
            goal15.goal = "Exploring Security Implications of Mobile Device Operating Systems";
            goal15.topic = topic5;
            goal15.maxBloom = BloomLevel.UNDERSTANDING;
            goal15.persist();

            LearningGoal goal16 = new LearningGoal();
            goal16.description = "Explore the security implications and challenges of ad-hoc networks and sensor networks.";
            goal16.goal = "Understanding Security in Ad-hoc Networks and Sensor Networks";
            goal16.topic = topic5;
            goal16.maxBloom = BloomLevel.APPLYING;
            goal16.persist();

            LearningGoal goal17 = new LearningGoal();
            goal17.description = "Understand how security has become an integral part of most areas of computing and computer science.";
            goal17.goal = "Recognizing the Integral Role of Security in Computing";
            goal17.topic = topic5;
            goal16.maxBloom = BloomLevel.ANALYZING;
            goal17.persist();

// Create Learning Goals for Topic 6 (Data and Application Security)

            LearningGoal goal18 = new LearningGoal();
            goal18.description = "Understand the security considerations for data, applications, and web service APIs.";
            goal18.goal = "Exploring Data and Application Security";
            goal18.topic = topic6;
            goal18.persist();

            LearningGoal goal19 = new LearningGoal();
            goal19.description = "Explore the legal and data protection issues related to IT security.";
            goal19.goal = "Understanding Legal and Data Protection Issues";
            goal19.topic = topic6;
            goal19.persist();

            LearningGoal goal20 = new LearningGoal();
            goal20.description = "Understand the significance of network security and the challenges of securing distributed applications.";
            goal20.goal = "Recognizing the Importance of Network Security";
            goal20.topic = topic6;
            goal20.persist();

            LearningGoal goal21 = new LearningGoal();
            goal21.description = "Understand the challenges and implications of 'shadow cloud' solutions and ill-managed IT systems.";
            goal21.goal = "Exploring Cloud Security and Ill-managed IT Systems";
            goal21.topic = topic6;
            goal21.persist();

// Create Learning Goals for Topic 7 (Roles and Security Management)

            LearningGoal goal22 = new LearningGoal();
            goal22.description = "Understand the concept of modeling high-level roles in an organization and the parameterization of roles.";
            goal22.goal = "Modeling High-level Roles in an Organization";
            goal22.topic = topic7;
            goal22.persist();

            LearningGoal goal23 = new LearningGoal();
            goal23.description = "Understand the concept of role-based access control and its significance in security management.";
            goal23.goal = "Exploring Role-based Access Control";
            goal23.topic = topic7;
            goal23.persist();

            LearningGoal goal24 = new LearningGoal();
            goal24.description = "Understand the significance of security awareness and training in ensuring a high level of information security within an organization.";
            goal24.goal = "Recognizing the Importance of Security Awareness and Training";
            goal24.topic = topic7;
            goal24.persist();

// Create Learning Goals for Topic 8 (Security Policy and Management)

            LearningGoal goal25 = new LearningGoal();
            goal25.description = "Learn to develop security policies based on ISO 27001 and ISO 27002 standards.";
            goal25.goal = "Developing Security Policies based on ISO Standards";
            goal25.topic = topic8;
            goal25.persist();

            LearningGoal goal26 = new LearningGoal();
            goal26.description = "Explore the requirements and standards for information security management, such as ISO 27001.";
            goal26.goal = "Understanding Information Security Management Standards";
            goal26.topic = topic8;
            goal26.maxBloom = BloomLevel.CREATING;
            goal26.persist();

            LearningGoal goal27 = new LearningGoal();
            goal27.description = "Understand the significance of security policy in organizations and its role in ensuring information security.";
            goal27.goal = "Recognizing the Importance of Security Policy in Organizations";
            goal27.topic = topic8;
            goal27.maxBloom = BloomLevel.CREATING;
            goal27.persist();


            // Task 1 - Multiple Choice: CIA in IT Security
            MultipleChoiceTask itSecTask1 = new MultipleChoiceTask();
            itSecTask1.assessment = assessment1;
            itSecTask1.bloomLevel = BloomLevel.REMEMBERING;
            itSecTask1.correctAnswer = "{\"option1\":\"Confidentiality, Integrity, Availability\"}";
            itSecTask1.question = "What does the acronym CIA stand for in IT security?";
            itSecTask1.topic = topic4;  // Assuming the relevant topic
            itSecTask1.verified = false;
            Map<String, String> itSecOptions1 = new HashMap<>();
            itSecOptions1.put("option1", "Confidentiality, Integrity, Availability");
            itSecOptions1.put("option2", "Confidentiality, Information, Access");
            itSecOptions1.put("option3", "Control, Integrity, Availability");
            itSecTask1.setOptions(itSecOptions1);
            itSecTask1.persist();

// Task 2 - Free Text: Significance of Confidentiality in IT Security
            FreeTextTask itSecTask2 = new FreeTextTask();
            itSecTask2.assessment = assessment1;
            itSecTask2.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask2.correctAnswer = "{\"text\":\"Confidentiality ensures that sensitive information is accessed only by authorized individuals, protecting it from unauthorized access and breaches.\"}";
            itSecTask2.question = "Explain the significance of confidentiality in IT security.";
            itSecTask2.topic = topic5;  // Assuming the relevant topic
            itSecTask2.verified = false;
            itSecTask2.persist();

// Task 3 - Essay: Historical vs Current Approaches to IT Security
            FreeTextTask itSecTask3 = new FreeTextTask();
            itSecTask3.assessment = assessment1;
            itSecTask3.bloomLevel = BloomLevel.ANALYZING;
            itSecTask3.correctAnswer = "{\"text\":\"Historical approaches focused on provable security, emphasizing mathematical proofs of security properties, while current practices involve partitioning systems, detecting threats in real-time, and repairing vulnerabilities as they arise. For example, historical methods might include formal verification methods, while current practices involve intrusion detection systems.\"}";
            itSecTask3.question = "Discuss the differences between historical approaches to IT security and current practices. Include examples of each.";
            itSecTask3.topic = topic6;  // Assuming the relevant topic
            itSecTask3.verified = false;
            itSecTask3.persist();

// Task 4 - Multiple Choice: Goal of Data Protection
            MultipleChoiceTask itSecTask4 = new MultipleChoiceTask();
            itSecTask4.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask4.correctAnswer = "{\"option1\":\"To ensure the confidentiality, integrity, and availability of data\"}";
            itSecTask4.question = "What is the primary goal of data protection in IT security?";
            itSecTask4.topic = topic6;  // Assuming the relevant topic
            itSecTask4.verified = false;
            Map<String, String> itSecOptions4 = new HashMap<>();
            itSecOptions4.put("option1", "To ensure the confidentiality, integrity, and availability of data");
            itSecOptions4.put("option2", "To increase the speed of data processing");
            itSecOptions4.put("option3", "To reduce the cost of data storage");
            itSecTask4.setOptions(itSecOptions4);
            itSecTask4.persist();

            FreeTextTask itSecTask5 = new FreeTextTask();
            itSecTask5.assessment = assessment1;
            itSecTask5.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask5.correctAnswer = "{\"text\":\"Good commit messages provide clear and descriptive information about the changes made, making it easier to understand the history of the codebase.\"}";
            itSecTask5.question = "Explain the significance of good commit messages in Git.";
            itSecTask5.topic = topic5;  // Assuming the relevant topic
            itSecTask5.verified = false;
            itSecTask5.persist();

// Task 6 - Multiple Choice: Steps in Committing Changes in Git
            MultipleChoiceTask itSecTask6 = new MultipleChoiceTask();
            itSecTask6.bloomLevel = BloomLevel.APPLYING;
            itSecTask6.correctAnswer = "{\"option1\":\"Staging changes and then committing\"}";
            itSecTask6.question = "What are the two steps involved in committing changes in Git?";
            itSecTask6.topic = topic5;  // Assuming the relevant topic
            itSecTask6.verified = false;
            Map<String, String> itSecOptions6 = new HashMap<>();
            itSecOptions6.put("option1", "Staging changes and then committing");
            itSecOptions6.put("option2", "Modifying files and then committing");
            itSecOptions6.put("option3", "Creating a new branch and then committing");
            itSecTask6.setOptions(itSecOptions6);
            itSecTask6.persist();

// Task 7 - True/False: Cloning Repositories in Git

//            TrueFalseTask itSecTask7 = new TrueFalseTask();
//            itSecTask7.assessment = assessment1;
//            itSecTask7.bloomLevel = BloomLevel.REMEMBERING;
//            itSecTask7.correctAnswer = "{\"value\":\"true\"}";
//            itSecTask7.question = "Git allows for easy cloning of existing repositories from remote locations.";
//            itSecTask7.topic = topic6;  // Assuming the relevant topic
//            itSecTask7.verified = false;
//            itSecTask7.persist();

            // Task 10 - Multiple Choice: Role of Authentication in IT Security
            MultipleChoiceTask itSecTask10 = new MultipleChoiceTask();
            itSecTask10.assessment = assessment1;
            itSecTask10.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask10.correctAnswer = "{\"option1\":\"To verify the identity of users\"}";
            itSecTask10.question = "What is the primary role of authentication in IT security?";
            itSecTask10.topic = topic6;  // Assuming the relevant topic
            itSecTask10.verified = false;
            Map<String, String> itSecOptions10 = new HashMap<>();
            itSecOptions10.put("option1", "To verify the identity of users");
            itSecOptions10.put("option2", "To protect data from unauthorized access");
            itSecOptions10.put("option3", "To encrypt sensitive data");
            itSecTask10.setOptions(itSecOptions10);
            itSecTask10.persist();

// Task 11 - Free Text: Importance of Encryption in IT Security
            FreeTextTask itSecTask11 = new FreeTextTask();
            itSecTask11.assessment = assessment1;
            itSecTask11.bloomLevel = BloomLevel.ANALYZING;
            itSecTask11.correctAnswer = "{\"text\":\"Encryption protects sensitive data by converting it into a format that can only be read by authorized individuals.\"}";
            itSecTask11.question = "Explain the importance of encryption in IT security.";
            itSecTask11.topic = topic7;  // Assuming the relevant topic
            itSecTask11.verified = false;
            itSecTask11.persist();

// Task 12 - Short Answer: Common Encryption Algorithms
            ShortAnswerTask itSecTask12 = new ShortAnswerTask();
            itSecTask12.assessment = assessment1;
            itSecTask12.bloomLevel = BloomLevel.REMEMBERING;
            itSecTask12.correctAnswer = "{\"text\":\"AES, RSA, and DES\"}";
            itSecTask12.question = "What are some common encryption algorithms used in IT security?";
            itSecTask12.topic = topic7;  // Assuming the relevant topic
            itSecTask12.verified = false;
            itSecTask12.persist();

// Task 13 - Multiple Choice: Risk Assessment in IT Security
            MultipleChoiceTask itSecTask13 = new MultipleChoiceTask();
            itSecTask13.bloomLevel = BloomLevel.ANALYZING;
            itSecTask13.correctAnswer = "{\"option1\":\"To identify and evaluate security risks\"}";
            itSecTask13.question = "What is the purpose of risk assessment in IT security?";
            itSecTask13.topic = topic8;  // Assuming the relevant topic
            itSecTask13.verified = false;
            Map<String, String> itSecOptions13 = new HashMap<>();
            itSecOptions13.put("option1", "To identify and evaluate security risks");
            itSecOptions13.put("option2", "To monitor network traffic");
            itSecOptions13.put("option3", "To enforce security policies");
            itSecTask13.setOptions(itSecOptions13);
            itSecTask13.persist();

// Task 14 - True/False: Data Backups in IT Security
            TrueFalseTask itSecTask14 = new TrueFalseTask();
            itSecTask14.assessment = assessment1;
            itSecTask14.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask14.correctAnswer = "{\"value\":\"true\"}";
            itSecTask14.question = "Regular data backups are essential for preventing data loss in IT security.";
            itSecTask14.topic = topic8;  // Assuming the relevant topic
            itSecTask14.verified = false;
            itSecTask14.persist();

            // Task 15 - Essay: Historical vs Current Approaches to IT Security
            FreeTextTask itSecTask15 = new FreeTextTask();
            itSecTask15.bloomLevel = BloomLevel.ANALYZING;
            itSecTask15.correctAnswer = "{\"text\":\"Historical approaches focused on provable security, emphasizing mathematical proofs of security properties, while current practices involve partitioning systems, detecting threats in real-time, and repairing vulnerabilities as they arise. For example, historical methods might include formal verification methods, while current practices involve intrusion detection systems.\"}";
            itSecTask15.question = "Discuss the differences between historical approaches to IT security and current practices. Include examples of each.";
            itSecTask15.topic = topic6;  // Assuming the relevant topic
            itSecTask15.verified = false;
            itSecTask15.persist();

// Task 16 - True/False: Security and Safety in IT Systems
            TrueFalseTask itSecTask16 = new TrueFalseTask();
            itSecTask16.assessment = assessment1;
            itSecTask16.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask16.correctAnswer = "{\"value\":\"false\"}";
            itSecTask16.question = "True or False: Security and safety are interchangeable terms in the context of IT systems.";
            itSecTask16.topic = topic7;  // Assuming the relevant topic
            itSecTask16.verified = false;
            itSecTask16.persist();

// Task 17 - Multiple Choice: Security and Adversaries in IT
            MultipleChoiceTask itSecTask17 = new MultipleChoiceTask();
            itSecTask17.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask17.correctAnswer = "{\"option2\":\"Security is a continuous race between attackers and defenders.\"}";
            itSecTask17.question = "Which of the following strategies best describes the relationship between security and adversaries in IT?";
            itSecTask17.topic = topic8;  // Assuming the relevant topic
            itSecTask17.verified = false;
            Map<String, String> itSecOptions17 = new HashMap<>();
            itSecOptions17.put("option1", "Security measures are static and do not change.");
            itSecOptions17.put("option2", "Security is a continuous race between attackers and defenders.");
            itSecOptions17.put("option3", "Once a security measure is implemented, it is no longer necessary to update it.");
            itSecTask17.setOptions(itSecOptions17);
            itSecTask17.persist();

// Task 18 - True/False: Impact of Malware on Mobile Devices
            TrueFalseTask itSecTask18 = new TrueFalseTask();
            itSecTask18.assessment = assessment1;
            itSecTask18.bloomLevel = BloomLevel.REMEMBERING;
            itSecTask18.correctAnswer = "{\"value\":\"false\"}";
            itSecTask18.question = "True or False: Malware only affects personal computers and has no impact on mobile devices.";
            itSecTask18.topic = topic5;  // Assuming the relevant topic
            itSecTask18.verified = false;
            itSecTask18.persist();

            // Task 19 - Multiple Choice: Enhancing Security in Mobile Device Operating Systems
            MultipleChoiceTask itSecTask19 = new MultipleChoiceTask();
            itSecTask19.bloomLevel = BloomLevel.APPLYING;
            itSecTask19.correctAnswer = "{\"option1\":\"Regularly update the OS and apps\"}";
            itSecTask19.question = "Which of the following strategies would best enhance security in a mobile device operating system?";
            itSecTask19.topic = topic5;  // Assuming the relevant topic
            itSecTask19.verified = false;
            Map<String, String> itSecOptions19 = new HashMap<>();
            itSecOptions19.put("option1", "Regularly update the OS and apps");
            itSecOptions19.put("option2", "Disable all security features to improve performance");
            itSecOptions19.put("option3", "Only use the device in public Wi-Fi networks");
            itSecTask19.setOptions(itSecOptions19);
            itSecTask19.persist();

// Task 20 - True/False: Security Awareness Training for IT Staff Only
            TrueFalseTask itSecTask20 = new TrueFalseTask();
            itSecTask20.bloomLevel = BloomLevel.UNDERSTANDING;
            itSecTask20.correctAnswer = "{\"value\":\"false\"}";
            itSecTask20.question = "True or False: Security awareness training is only necessary for IT staff.";
            itSecTask20.topic = topic7;  // Assuming the relevant topic
            itSecTask20.verified = false;
            itSecTask20.persist();

// Task 21 - Essay: Security Implications of Ad-Hoc Networks
            FreeTextTask itSecTask21 = new FreeTextTask();
            itSecTask21.assessment = assessment1;
            itSecTask21.bloomLevel = BloomLevel.ANALYZING;
            itSecTask21.correctAnswer = "{\"text\":\"Ad-hoc networks can introduce several security risks, such as unauthorized access and data interception. To mitigate these risks, organizations should implement strong encryption protocols, use secure authentication methods, and limit the use of ad-hoc networks to trusted devices.\"}";
            itSecTask21.question = "Analyze the security implications of using ad-hoc networks in a corporate environment. Discuss potential risks and mitigation strategies.";
            itSecTask21.topic = topic5;  // Assuming the relevant topic
            itSecTask21.verified = false;
            itSecTask21.persist();

// Task 22 - Essay: Importance of Security Awareness and Training
            FreeTextTask itSecTask22 = new FreeTextTask();
            itSecTask22.assessment = assessment1;
            itSecTask22.bloomLevel = BloomLevel.EVALUATING;
            itSecTask22.correctAnswer = "{\"text\":\"Security awareness and training are crucial for ensuring that all employees understand the potential security threats and the best practices to mitigate them. This proactive approach can significantly reduce the risk of security breaches and enhance the overall security posture of the organization.\"}";
            itSecTask22.question = "Discuss the importance of security awareness and training in maintaining information security within an organization.";
            itSecTask22.topic = topic7;  // Assuming the relevant topic
            itSecTask22.verified = false;
            itSecTask22.persist();

// Task 23 - True/False: ISO 27001 and ISO 27002 Standards
            TrueFalseTask itSecTask23 = new TrueFalseTask();
            itSecTask23.bloomLevel = BloomLevel.REMEMBERING;
            itSecTask23.correctAnswer = "{\"value\":\"false\"}";
            itSecTask23.question = "True or False: ISO 27001 and ISO 27002 are interchangeable standards.";
            itSecTask23.topic = topic8;  // Assuming the relevant topic
            itSecTask23.verified = false;
            itSecTask23.persist();

            // Create Learning Path
            LearningPath learningPath = new LearningPath();
            learningPath.course = course;
            learningPath.user = user2;
            learningPath.initialAssessmentCompleted = false;
            learningPath.persist();

            // Create Learning Step
            LearningStep learningStep = new LearningStep();
            learningStep.learningPath = learningPath;
            learningStep.explanationText = "The student has demonstrated a good understanding of version control and the benefits of using Git. However, they made mistakes in recalling certain Git commands and misunderstood the purpose of some commands. Additionally, they need to reinforce their understanding of working with branches and explore more examples and best practices related to Git. The student also needs to engage in learning and practicing advanced Git features, including monitoring and logging, Git configuration, and best practices to improve workflow and avoid pitfalls.";
            learningStep.readingMaterial = "In this learning step, we will focus on reinforcing your understanding of Git commands and best practices. Let's start with the basics. Git provides commands to access and update the database of tracked files. It is widely used and has many powerful features, but it can also be challenging to use at times. GitLab, GitHub, and other platforms have formed around the Git core software, providing graphical user interfaces and integration in Integrated Design Environments. The purpose here is to familiarize you with the principles of version control, some good practices, and get you started on the practical matters.\n\nTo initialize a new local repository in Git, you can use the command 'git init'. This command creates a new Git repository. After initializing the repository, you can use 'ls -a' to see the files in the repository, both tracked and untracked by Git.\n\nCommitting in Git works in two steps. First, modified or untracked files are 'registered' for the next commit, followed by staging changes and then committing. Commits are a saved snapshot of tracked files, and you can always revert to a commit. Good commit messages provide clear and descriptive information about the changes made, making it easier to understand the history of the codebase.\n\nWorking with branches in Git is essential for collaborative development. You can create, switch, and merge branches in Git. It's important to understand the purpose of the 'git pull' command, which is used to fetch and download content from a remote repository and immediately update the local repository to match that content. Additionally, you can use the 'git push' command to push changes to the remote repository.\n\nIn the advanced features of Git, you will learn about monitoring and logging, Git configuration, and best practices to improve workflow and avoid pitfalls. Engaging in tutorials, documentation, and practical examples will enhance your knowledge in this area.\n\nOverall, Git allows for easy collaboration, efficient branching and merging, and provides a complete history of changes made to the code. It supports incremental development, allows for comparing and reverting to earlier versions, provides backup, manages parallel versions, and documents development for other developers and yourself. It is essential to use version control all the time for efficient and organized software development.";
            learningStep.persist();

            // Link Tasks to Learning Step
            learningStep.tasks.add(task1);
            learningStep.tasks.add(task2);
            learningStep.tasks.add(task3);
            learningStep.tasks.add(task4);
            learningStep.tasks.add(task5);
            learningStep.tasks.add(task6);
            learningStep.tasks.add(task7);
            learningStep.tasks.add(task8);
            learningStep.tasks.add(task9);
            learningStep.persist();

            // Create LearningStatistics after initial assessment (3 days ago)
            createLearningStatistics(user2, learningPath, null, topic1, BloomLevel.REMEMBERING, 3600, 3);
            createLearningStatistics(user2, learningPath, null, topic2, BloomLevel.NONE, 0, 3);
            createLearningStatistics(user2, learningPath, null, topic3, BloomLevel.NONE, 0, 3);
            createLearningStatistics(user2, learningPath, null, topic4, BloomLevel.NONE, 0, 3);
            createLearningStatistics(user2, learningPath, null, topic5, BloomLevel.NONE, 0, 3);

            // Create LearningStatistics after first learning step (today)
            createLearningStatistics(user2, learningPath, learningStep, topic1, BloomLevel.APPLYING, 7200, 0);
            createLearningStatistics(user2, learningPath, learningStep, topic2, BloomLevel.UNDERSTANDING, 5400, 0);
            createLearningStatistics(user2, learningPath, learningStep, topic3, BloomLevel.NONE, 0, 0);
            createLearningStatistics(user2, learningPath, learningStep, topic4, BloomLevel.NONE, 0, 0);
            createLearningStatistics(user2, learningPath, learningStep, topic5, BloomLevel.NONE, 0, 0);


//            ADDITIONAL USER LEARNINGPATH AND STATISTICS

            // Create Learning Path and Learning Steps for user1
            LearningPath learningPath1 = new LearningPath();
            learningPath1.course = course;
            learningPath1.user = user1;
            learningPath1.initialAssessmentCompleted = false;
            learningPath1.persist();

            LearningStep learningStep1 = new LearningStep();
            learningStep1.learningPath = learningPath1;
            learningStep1.explanationText = "Explanation for User 1";
            learningStep1.readingMaterial = "Reading Material for User 1";
            learningStep1.persist();

// Create Learning Statistics for user1
            createLearningStatistics(user1, learningPath1, null, topic1, BloomLevel.REMEMBERING, 3000, 10);
            createLearningStatistics(user1, learningPath1, null, topic2, BloomLevel.NONE, 0, 10);
            createLearningStatistics(user1, learningPath1, null, topic3, BloomLevel.NONE, 0, 10);
            createLearningStatistics(user1, learningPath1, null, topic4, BloomLevel.NONE, 0, 10);
            createLearningStatistics(user1, learningPath1, null, topic5, BloomLevel.NONE, 0, 10);

// Add statistics for a learning step on a later date
            createLearningStatistics(user1, learningPath1, learningStep1, topic1, BloomLevel.UNDERSTANDING, 5000, 5);
            createLearningStatistics(user1, learningPath1, learningStep1, topic2, BloomLevel.REMEMBERING, 2000, 5);
            createLearningStatistics(user1, learningPath1, learningStep1, topic3, BloomLevel.NONE, 0, 5);
            createLearningStatistics(user1, learningPath1, learningStep1, topic4, BloomLevel.NONE, 0, 5);
            createLearningStatistics(user1, learningPath1, learningStep1, topic5, BloomLevel.NONE, 0, 5);

// Add statistics for a learning step today
            createLearningStatistics(user1, learningPath1, learningStep1, topic1, BloomLevel.ANALYZING, 7000, 0);
            createLearningStatistics(user1, learningPath1, learningStep1, topic2, BloomLevel.UNDERSTANDING, 4500, 0);
            createLearningStatistics(user1, learningPath1, learningStep1, topic3, BloomLevel.REMEMBERING, 3000, 0);
            createLearningStatistics(user1, learningPath1, learningStep1, topic4, BloomLevel.NONE, 0, 0);
            createLearningStatistics(user1, learningPath1, learningStep1, topic5, BloomLevel.NONE, 0, 0);

// Create Learning Path and Learning Steps for user3
            LearningPath learningPath3 = new LearningPath();
            learningPath3.course = course;
            learningPath3.user = user3;
            learningPath3.initialAssessmentCompleted = false;
            learningPath3.persist();

            LearningStep learningStep3 = new LearningStep();
            learningStep3.learningPath = learningPath3;
            learningStep3.explanationText = "Explanation for User 3";
            learningStep3.readingMaterial = "Reading Material for User 3";
            learningStep3.persist();

// Create Learning Statistics for user3
            createLearningStatistics(user3, learningPath3, null, topic1, BloomLevel.NONE, 0, 20);
            createLearningStatistics(user3, learningPath3, null, topic2, BloomLevel.NONE, 0, 20);
            createLearningStatistics(user3, learningPath3, null, topic3, BloomLevel.NONE, 0, 20);
            createLearningStatistics(user3, learningPath3, null, topic4, BloomLevel.NONE, 0, 20);
            createLearningStatistics(user3, learningPath3, null, topic5, BloomLevel.NONE, 0, 20);

// Add statistics for a learning step on a later date
            createLearningStatistics(user3, learningPath3, learningStep3, topic1, BloomLevel.REMEMBERING, 2000, 15);
            createLearningStatistics(user3, learningPath3, learningStep3, topic2, BloomLevel.NONE, 0, 15);
            createLearningStatistics(user3, learningPath3, learningStep3, topic3, BloomLevel.NONE, 0, 15);
            createLearningStatistics(user3, learningPath3, learningStep3, topic4, BloomLevel.NONE, 0, 15);
            createLearningStatistics(user3, learningPath3, learningStep3, topic5, BloomLevel.NONE, 0, 15);

// Add statistics for a learning step today
            createLearningStatistics(user3, learningPath3, learningStep3, topic1, BloomLevel.UNDERSTANDING, 4000, 0);
            createLearningStatistics(user3, learningPath3, learningStep3, topic2, BloomLevel.REMEMBERING, 1500, 0);
            createLearningStatistics(user3, learningPath3, learningStep3, topic3, BloomLevel.NONE, 0, 0);
            createLearningStatistics(user3, learningPath3, learningStep3, topic4, BloomLevel.NONE, 0, 0);
            createLearningStatistics(user3, learningPath3, learningStep3, topic5, BloomLevel.NONE, 0, 0);


//            END ADDITIONAL USER LEARNINGPATH AND STATSITIC


            // Create additional Learning Steps
            LearningStep learningStep2 = new LearningStep();
            learningStep2.explanationText = "The student has demonstrated understanding of the basics of Git, including the purpose and benefits of version control, as well as the process of committing and branching. However, there are weaknesses in understanding the benefits of using Git as a version control system, the purpose of the 'git pull' command, and advanced Git features. To address these weaknesses, the student needs to focus on understanding the benefits of Git in collaborative working, efficient branching, and merging. Additionally, they should revisit the purpose of the 'git pull' command and dedicate more time to studying and practicing advanced Git features, best practices, and common issues in Git.\n\n---\n\nThe student has demonstrated a good understanding of committing changes and working with branches in Git. However, they have weaknesses in understanding the purpose of the 'git pull' command and common issues in Git. It is important for the student to focus on understanding the benefits of branches, merging branches, and the use of Git in agile collaborative working to address these weaknesses.\n\n---\n\nThe student's weaknesses in the 'Advanced Git Features' topic include a lack of understanding of advanced features such as monitoring changes, viewing commit history, using logging, configuring Git settings, and managing user information. Additionally, they need to explore common issues and best practices in Git to improve their workflow and avoid pitfalls. It is essential for the student to focus on understanding the benefits of using Git as a version control system, particularly in terms of collaboration, efficient branching, merging, and providing a complete history of changes. They should also revisit the significance of good commit messages to ensure a better understanding and practice good branching strategies and descriptive commit messages to enhance their understanding of committing and branching.\n\n---\n\n";
            learningStep2.learningPath = learningPath;
            learningStep2.previousStep = learningStep;
//            learningStep2.readingMaterial = "In order to address the identified weaknesses and reinforce the student's strengths, it is essential to delve into the benefits of branches, merging branches, and the use of Git in agile collaborative working. Let's start by understanding the benefits of branches in Git. Branches in Git provide a way to work on new features, bug fixes, or experiments without affecting the main codebase. They allow developers to isolate their work and make changes independently, which can then be merged back into the main codebase. This approach helps in maintaining a clean and organized codebase and facilitates parallel development. Moreover, branches enable collaboration among team members by allowing them to work on different features simultaneously without interfering with each other's work. This leads to increased productivity and efficient teamwork. Now, let's explore the process of merging branches in Git. Merging is the act of integrating changes from one branch into another. It allows developers to combine the work done in different branches, ensuring that all changes are incorporated into the main codebase. Merging branches is essential for consolidating the progress made in various development efforts and ensuring that the final codebase is up to date and functional. Additionally, merging branches helps in resolving conflicts that may arise when multiple developers make changes to the same codebase. Moving on to the use of Git in agile collaborative working, Git plays a crucial role in enabling agile development practices. It allows teams to work concurrently on different aspects of a project, facilitating rapid iterations and feedback cycles. Git's branching and merging capabilities support the agile principle of delivering working software frequently, enabling teams to implement new features and address issues in an iterative manner. Moreover, Git provides a complete history of changes, making it easier to track progress and roll back to previous versions if needed. This ensures transparency and accountability in the development process, aligning with the collaborative nature of agile methodologies. Overall, understanding the benefits of branches, merging branches, and the use of Git in agile collaborative working is crucial for efficient and effective software development in a collaborative environment.\n\n---\n\nIn order to address the weaknesses identified, let's delve into the benefits of branches, merging branches, and the use of Git in agile collaborative working. Understanding the benefits of branches in Git includes the ability to work on new features or fixes without affecting the main codebase, enabling parallel development, and facilitating code review and testing before merging changes into the main branch. Merging branches in Git involves combining the changes from one branch into another, typically used to integrate feature branches into the main branch. This process ensures that the changes are properly combined and conflicts are resolved. Additionally, Git plays a crucial role in agile collaborative working by providing a distributed version control system that allows multiple developers to work on the same project simultaneously, facilitating seamless collaboration, efficient branching and merging, and maintaining a complete history of changes. It also enables the implementation of continuous integration and deployment practices, ensuring that changes are integrated and tested frequently, leading to a more agile and responsive development process.\n\n---\n\nIn order to improve your understanding of advanced Git features, it's important to delve into the benefits of using Git as a version control system and its significance in collaboration, efficient branching, merging, and providing a complete history of changes. Git allows multiple developers to work on the same project simultaneously without interfering with each other's changes. This collaborative approach enhances productivity and enables efficient teamwork. Additionally, Git's branching and merging capabilities provide a structured way to work on new features, bug fixes, or experiments without affecting the main codebase. Each branch represents an independent line of development, allowing changes to be isolated and tested before being merged back into the main branch. This process ensures that the main codebase remains stable and unaffected by ongoing development work. Understanding the benefits of branching and merging is crucial for effective version control and project management. Furthermore, Git's ability to provide a complete history of changes allows developers to track the evolution of the codebase, understand the context of specific changes, and revert to previous states if needed. This feature is invaluable for maintaining code quality and ensuring the stability of the project. Moving on to the advanced features, monitoring changes, viewing commit history, and using logging in Git are essential for tracking the development progress, identifying issues, and understanding the project's evolution over time. Monitoring changes allows developers to stay informed about modifications made to the codebase, ensuring transparency and visibility across the team. Viewing commit history provides insights into the timeline of changes, the contributors involved, and the specific modifications made, facilitating effective collaboration and accountability. Logging in Git enables the recording of important events and actions, helping to diagnose problems, track changes, and maintain an audit trail of activities. Additionally, configuring Git settings and managing user information are fundamental for customizing the development environment and ensuring accurate attribution of contributions. Understanding these advanced features and best practices in Git will significantly enhance your workflow, improve collaboration, and mitigate common issues, ultimately leading to more efficient and successful project management.\n\n---\n\n";
            learningStep2.readingMaterial = "<h2>Addressing Weaknesses and Reinforcing Strengths</h2>\n" +
                    "    <p>\n" +
                    "        In order to address the identified weaknesses and reinforce the student's strengths, it is essential to delve into the benefits of branches, merging branches, and the use of Git in agile collaborative working. Let's start by understanding the benefits of branches in Git. Branches in Git provide a way to work on new features, bug fixes, or experiments without affecting the main codebase. They allow developers to isolate their work and make changes independently, which can then be merged back into the main codebase. This approach helps in maintaining a clean and organized codebase and facilitates parallel development. Moreover, branches enable collaboration among team members by allowing them to work on different features simultaneously without interfering with each other's work. This leads to increased productivity and efficient teamwork.\n" +
                    "    </p>\n" +
                    "    \n" +
                    "    <h2>The Process of Merging Branches in Git</h2>\n" +
                    "    <p>\n" +
                    "        Now, let's explore the process of merging branches in Git. Merging is the act of integrating changes from one branch into another. It allows developers to combine the work done in different branches, ensuring that all changes are incorporated into the main codebase. Merging branches is essential for consolidating the progress made in various development efforts and ensuring that the final codebase is up to date and functional. Additionally, merging branches helps in resolving conflicts that may arise when multiple developers make changes to the same codebase.\n" +
                    "    </p>\n" +
                    "    \n" +
                    "    <h2>Git in Agile Collaborative Working</h2>\n" +
                    "    <p>\n" +
                    "        Moving on to the use of Git in agile collaborative working, Git plays a crucial role in enabling agile development practices. It allows teams to work concurrently on different aspects of a project, facilitating rapid iterations and feedback cycles. Git's branching and merging capabilities support the agile principle of delivering working software frequently, enabling teams to implement new features and address issues in an iterative manner. Moreover, Git provides a complete history of changes, making it easier to track progress and roll back to previous versions if needed. This ensures transparency and accountability in the development process, aligning with the collaborative nature of agile methodologies. Overall, understanding the benefits of branches, merging branches, and the use of Git in agile collaborative working is crucial for efficient and effective software development in a collaborative environment.\n" +
                    "    </p>\n" +
                    "    \n" +
                    "    <hr>\n" +
                    "\n" +
                    "    <h2>Addressing Identified Weaknesses</h2>\n" +
                    "    <p>\n" +
                    "        In order to address the weaknesses identified, let's delve into the benefits of branches, merging branches, and the use of Git in agile collaborative working. Understanding the benefits of branches in Git includes the ability to work on new features or fixes without affecting the main codebase, enabling parallel development, and facilitating code review and testing before merging changes into the main branch. Merging branches in Git involves combining the changes from one branch into another, typically used to integrate feature branches into the main branch. This process ensures that the changes are properly combined and conflicts are resolved.\n" +
                    "    </p>\n" +
                    "    <p>\n" +
                    "        Additionally, Git plays a crucial role in agile collaborative working by providing a distributed version control system that allows multiple developers to work on the same project simultaneously, facilitating seamless collaboration, efficient branching and merging, and maintaining a complete history of changes. It also enables the implementation of continuous integration and deployment practices, ensuring that changes are integrated and tested frequently, leading to a more agile and responsive development process.\n" +
                    "    </p>\n" +
                    "    \n" +
                    "    <hr>\n" +
                    "\n" +
                    "    <h2>Understanding Advanced Git Features</h2>\n" +
                    "    <p>\n" +
                    "        In order to improve your understanding of advanced Git features, it's important to delve into the benefits of using Git as a version control system and its significance in collaboration, efficient branching, merging, and providing a complete history of changes. Git allows multiple developers to work on the same project simultaneously without interfering with each other's changes. This collaborative approach enhances productivity and enables efficient teamwork.\n" +
                    "    </p>\n" +
                    "    <p>\n" +
                    "        Additionally, Git's branching and merging capabilities provide a structured way to work on new features, bug fixes, or experiments without affecting the main codebase. Each branch represents an independent line of development, allowing changes to be isolated and tested before being merged back into the main branch. This process ensures that the main codebase remains stable and unaffected by ongoing development work. Understanding the benefits of branching and merging is crucial for effective version control and project management.\n" +
                    "    </p>\n" +
                    "    <p>\n" +
                    "        Furthermore, Git's ability to provide a complete history of changes allows developers to track the evolution of the codebase, understand the context of specific changes, and revert to previous states if needed. This feature is invaluable for maintaining code quality and ensuring the stability of the project. Moving on to the advanced features, monitoring changes, viewing commit history, and using logging in Git are essential for tracking the development progress, identifying issues, and understanding the project's evolution over time. Monitoring changes allows developers to stay informed about modifications made to the codebase, ensuring transparency and visibility across the team.\n" +
                    "    </p>\n" +
                    "    <p>\n" +
                    "        Viewing commit history provides insights into the timeline of changes, the contributors involved, and the specific modifications made, facilitating effective collaboration and accountability. Logging in Git enables the recording of important events and actions, helping to diagnose problems, track changes, and maintain an audit trail of activities. Additionally, configuring Git settings and managing user information are fundamental for customizing the development environment and ensuring accurate attribution of contributions. Understanding these advanced features and best practices in Git will significantly enhance your workflow, improve collaboration, and mitigate common issues, ultimately leading to more efficient and successful project management.\n" +
                    "    </p>";
            learningStep2.persist();

            // Update nextStep reference in first learning step
            learningStep.nextStep = learningStep2;
            learningStep.persist();

            // Add tasks to the second learning step
            // Assuming task10, task11, task12, etc. have already been created
            learningStep2.tasks.add(task1);
            learningStep2.tasks.add(task2);
            learningStep2.tasks.add(task3);
            learningStep2.tasks.add(task4);  // Assuming these are the new tasks
            learningStep2.tasks.add(task5);
            learningStep2.tasks.add(task6);
            learningStep2.tasks.add(task7);
            learningStep2.tasks.add(task8);
            learningStep2.tasks.add(task9);
            learningStep2.persist();


            // Link User to Course
            user2.courses.add(course);
            user2.persist();

            // Create user task attempts and answers
            createTaskAttempt(user2, task1, assessment, null, "To collaborate with team members on code", false, "The purpose of version control in software development is to manage different versions of the software, allowing for collaboration with team members, understanding changes, supporting incremental development, comparing and reverting to earlier versions, creating backups, and maintaining parallel versions. It also helps document development for other developers and yourself. Version control, such as Git, provides commands to access and update the database of tracked files and supports graphical user interfaces, integration in Integrated Design Environments, and web platforms like GitHub and GitLab. It also allows for the creation of branches for parallel development and facilitates the merging of changes from different branches. Overall, version control is essential for efficient and organized software development.");

            createTaskAttempt(user2, task2, assessment, null, "versioning", true, "The user's solution is correct. Git indeed provides benefits such as easy collaboration, efficient branching and merging, and a complete history of changes made to the code. Additionally, Git supports incremental development, allows for comparing and reverting to earlier versions, provides backup, manages parallel versions, and documents development for developers. The user has a good understanding of the benefits of using Git as a version control system.");

            createTaskAttempt(user2, task3, assessment, null, "giot inti", false, "The command to initialize a new local repository in Git is 'git init', not 'giot inti'. Make sure to use the correct command to initialize a new local repository in Git.");

            createTaskAttempt(user2, task4, assessment, null, "false", true, "The user's solution is correct. Commits in Git are indeed identified by a unique hexadecimal number (a hash).");

            createTaskAttempt(user2, task5, assessment, null, "so ai can make api calls", false, "The user's solution does not explain the significance of good commit messages in Git. Good commit messages provide clear and descriptive information about the changes made, making it easier to understand the history of the codebase. It helps in tracking changes, understanding the context of the changes, and collaborating with other developers. It is important to provide meaningful and descriptive commit messages to improve the overall understanding and maintainability of the codebase.");

            createTaskAttempt(user2, task6, assessment, null, "Staging changes and then committing", true, "The user's solution is correct. The two steps involved in committing changes in Git are indeed staging changes and then committing. Staging changes is done using the 'git add' command, and then the staged files are committed using the 'git commit' command. Well done!");

            createTaskAttempt(user2, task7, assessment, null, "true", true, "The user's solution is correct. Git does allow for easy cloning of existing repositories from remote locations using various protocols such as http(s), ssh, etc. The user has provided the correct information about cloning a repository from GitLab using the 'git clone' command and has also mentioned the commands for setting configurations, updating the local and remote repositories, and working with remote repositories. Overall, the user has demonstrated a good understanding of cloning repositories in Git.");

            createTaskAttempt(user2, task8, assessment, null, "to push changes to remote", false, "The purpose of the command 'git pull' is to fetch and download content from a remote repository and immediately update the local repository to match that content. It is not used to push changes to the remote repository. The 'git push' command is used to push changes to the remote repository. The 'git pull' command is important for keeping the local repository up to date with changes made in the remote repository.");

            createTaskAttempt(user2, task9, assessment, null, "merge conflicts. you can avoid by just using one branch", false, "The user's solution is partially correct. They mentioned merge conflicts as a common issue in Git, which is correct. However, they missed the mention of accidental commits as another common issue. Additionally, the user's proposed solution of using only one branch to avoid merge conflicts is not comprehensive. The correct approach to avoid common issues in Git includes practicing good branching strategies and using descriptive commit messages.");

            createTaskAttempt(user2, task1, null, learningStep, "To manage different versions of the software", true, "The purpose of version control in software development is to manage different versions of the software, allowing for collaboration with team members, understanding changes, supporting incremental development, comparing and reverting to earlier versions, creating backups, and maintaining parallel versions. It also helps document development for other developers and yourself. Version control, such as Git, provides commands to access and update the database of tracked files and supports graphical user interfaces, integration in Integrated Design Environments, and web platforms like GitHub and GitLab. It also allows for the creation of branches for parallel development and facilitates the merging of changes from different branches. Overall, version control is essential for efficient and organized software development.");
            createTaskAttempt(user2, task2, null, learningStep, "-", false, "The correct answer is: Git allows for easy collaboration, efficient branching and merging, and provides a complete history of changes made to the code. The provided answer 'incorrect answer for task2' is not sufficient.");
            createTaskAttempt(user2, task3, null, learningStep, "git init", true, "The command to initialize a new local repository in Git is 'git init'.");
            createTaskAttempt(user2, task4, null, learningStep, "true", true, "The user's solution is correct. Commits in Git are indeed identified by a unique hexadecimal number (a hash).");
            createTaskAttempt(user2, task5, null, learningStep, "incorrect answer for task5", false, "The correct answer is: Good commit messages provide clear and descriptive information about the changes made, making it easier to understand the history of the codebase. The provided answer 'incorrect answer for task5' is not sufficient.");
            createTaskAttempt(user2, task6, null, learningStep, "Staging changes and then committing", true, "The user's solution is correct. The two steps involved in committing changes in Git are indeed staging changes and then committing. Staging changes is done using the 'git add' command, and then the staged files are committed using the 'git commit' command. Well done!");
            createTaskAttempt(user2, task7, null, learningStep, "true", true, "The user's solution is correct. Git does allow for easy cloning of existing repositories from remote locations using various protocols such as http(s), ssh, etc. The user has provided the correct information about cloning a repository from GitLab using the 'git clone' command and has also mentioned the commands for setting configurations, updating the local and remote repositories, and working with remote repositories. Overall, the user has demonstrated a good understanding of cloning repositories in Git.");
            createTaskAttempt(user2, task8, null, learningStep, "-", false, "The correct answer is: The 'git pull' command is used to fetch and download content from a remote repository and immediately update the local repository to match that content. The provided answer 'incorrect answer for task8' is not sufficient.");
            createTaskAttempt(user2, task9, null, learningStep, "incorrect answer for task9", false, "The correct answer is: Common issues in Git include merge conflicts and accidental commits. These can be avoided by practicing good branching strategies and using descriptive commit messages. The provided answer 'incorrect answer for task9' is not sufficient.");
            populateInitialData(course);


            return Response.ok("Test data initialized successfully").build();
        } catch (Exception e) {
            LOG.error("Error initializing test data", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing test data: " + e.getMessage()).build();
        }
    }

    private void createTaskAttempt(User user, Task task, Assessment assessment, LearningStep learningStep, String answer, boolean isCorrect, String feedback) {
        UserTaskAttempt attempt = new UserTaskAttempt();
        attempt.user = user;
        attempt.task = task;
        attempt.assessment = assessment;
        attempt.learningStep = learningStep;
        attempt.attemptTime = LocalDateTime.now();
        attempt.isCorrect = isCorrect;
//        attempt.learningPath =
        attempt.persist();

        TaskAnswer taskAnswer = new TaskAnswer();
        taskAnswer.answer = answer;
        taskAnswer.attempt = attempt;
        taskAnswer.llmResponse = feedback;
        taskAnswer.persist();
    }

    private void createLearningStatistics(User user, LearningPath learningPath, LearningStep learningStep, Topic topic, BloomLevel currentBloomLevel, long timeSpent, int daysAgo) {
        LearningStatistics statistics = new LearningStatistics();
        statistics.user = user;
        statistics.learningPath = learningPath;
        statistics.learningStep = learningStep;
        statistics.topic = topic;
        statistics.currentBloomLevel = currentBloomLevel;
        statistics.timeSpent = timeSpent;
        statistics.strengths = ""; // You can add some strengths if desired
        statistics.weaknesses = ""; // You can add some weaknesses if desired
        statistics.recommendations = ""; // You can add some recommendations if desired
        statistics.recordedAt = LocalDateTime.now().minusDays(daysAgo); // Set recorded date based on daysAgo
        statistics.persist();
    }

    public User createRandomUser() {
        User user = new User();
        user.email = UUID.randomUUID().toString() + "@example.com"; // Unique email
        user.firstName = "User" + UUID.randomUUID().toString().substring(0, 5); // Random first name
        user.lastName = "Lastname" + UUID.randomUUID().toString().substring(0, 5); // Random last name
        user.password = "password123"; // Default password for testing
        user.studentNumber = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999)); // Random student number
        user.persist();
        return user;
    }

    public void populateInitialData(Course course) {
        List<User> users = new ArrayList<>();

        // Step 1: Generate and enroll 15 random users
        for (int i = 0; i < 10; i++) {
            User user = createRandomUser();
            userService.enrollUserToCourse(user.id, course.id);
            users.add(user);
        }

        // Step 2: Create Learning Paths and Learning Statistics for each user
        for (User user : users) {
            // Fetch the learning path created during enrollment
            LearningPath learningPath = user.learningPaths.stream()
                    .filter(lp -> lp.course.id.equals(course.id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Learning Path not found for user"));

            // Randomly select how many days the user worked
            int activeDays = ThreadLocalRandom.current().nextInt(3, 9); // Each user will work between 3 and 8 days
            List<Integer> workDays = new ArrayList<>();

            for (int j = 0; j < activeDays; j++) {
                int day = ThreadLocalRandom.current().nextInt(0, 15); // Random days within 15-day span
                if (!workDays.contains(day)) {
                    workDays.add(day);
                }
            }

            for (Integer day : workDays) {
                // Create Learning Step (optional - can be reused or created per user)
                LearningStep learningStep = new LearningStep();
                learningStep.learningPath = learningPath;
                learningStep.explanationText = "Explanation for User " + user.firstName;
                learningStep.readingMaterial = "Reading Material for User " + user.firstName;
                learningStep.persist();

                // Create Learning Statistics
                BloomLevel[] bloomLevels = BloomLevel.values();
                for (Topic topic : course.topics) {
                    BloomLevel currentBloomLevel = bloomLevels[ThreadLocalRandom.current().nextInt(bloomLevels.length)];
                    createLearningStatistics(user, learningPath, learningStep, topic, currentBloomLevel, ThreadLocalRandom.current().nextInt(0, 7200), day);
                }
            }
        }
    }


    @POST
    @Path("user")
    @Transactional
    public Response initUsers() {
        try {
            // Create Users
            User user1 = new User();
            user1.email = "user1@gmail.com";
            user1.firstName = "User";
            user1.lastName = "User";
            user1.password = "123456";
            user1.studentNumber = "123456";
            user1.role = "student";
            user1.preferredLanguage = "deutsch";
            user1.persist();

            User user2 = new User();
            user2.email = "tobiasbrenner2@gmail.com";
            user2.firstName = "Tobi";
            user2.lastName = "Brenner";
            user2.password = "admin123";
            user2.studentNumber = "12345";
            user2.role = "teacher";
            user2.preferredLanguage = "deutsch";
            user2.persist();

            User user3 = new User();
            user3.email = "user3@gmail.com";
            user3.firstName = "Prof";
            user3.lastName = "Prof";
            user3.password = "hashed_password_here";
            user3.studentNumber = "123455";
            user3.role = "teacher";
            user3.preferredLanguage = "deutsch";
            user3.persist();


            User user4 = new User();
            user4.email = "user4@gmail.com";
            user4.firstName = "Prof";
            user4.lastName = "Prof";
            user4.password = "hashed_password_here";
            user4.studentNumber = "123455";
            user4.role = "teacher";
            user4.preferredLanguage = "deutsch";
            user4.persist();

            User user5 = new User();
            user5.email = "user5@gmail.com";
            user5.firstName = "Prof";
            user5.lastName = "Prof";
            user5.password = "hashed_password_here";
            user5.studentNumber = "123455";
            user5.role = "teacher";
            user5.preferredLanguage = "deutsch";
            user5.persist();
        } catch (Exception e) {
            LOG.error("Error initializing test data", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing test data: " + e.getMessage()).build();
        }
        return Response.ok("Test data initialized successfully").build();
    }
}
