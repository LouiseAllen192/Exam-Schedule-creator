import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class is14175819 {
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> inputs;
        ArrayList<HashSet<Integer>> studentModuleInfo;
        ArrayList<int[][]> orderings;


        inputs = requestInputs();
        if (inputs != null) {
            studentModuleInfo = generateStudentModuleInfo(inputs.get("S"), inputs.get("C"), inputs.get("M"));
            printStudentModuleInfo(studentModuleInfo);

            int requestedPopulationSize = inputs.get("P");

            //For M total number of modules there are a limited amount of unique permutations.
            // We will limit the population size P to the factorial of M (less than 800)
            int  maxPopulationSize = getMaxPopulationSize(inputs.get("M"));
            if (requestedPopulationSize > maxPopulationSize) {
                inputs.put("P", maxPopulationSize);
            }

            orderings = generateOrderings(inputs, studentModuleInfo);
            printOrderings(orderings);

            if (inputs.get("P") != requestedPopulationSize) {
                printWarningForAdjustedPopulationSize(requestedPopulationSize, maxPopulationSize, inputs.get("M"));
            }
        }
    }

    private static HashMap<String, Integer> requestInputs() {
        HashMap<String, Integer> hmap = new HashMap<String,Integer>();
        String promptBeginning = "Please enter the ";
        String standardErrorMessage = "Invalid input. Please enter an integer\n";
        Scanner scan = new Scanner(System.in);

        boolean quit = false;
        String[] keys = {"G", "P", "S", "M", "C"};
        String[] prompts = {"number of generations", "population size", "number of students", "total number of modules", "the number of modules per course"};
        int indexOfTotalNumModulesPrompt = 3;
        int indexOfNumModulesPerCourse = 4;
        for (int i = 0; i < prompts.length && !quit; i++) {
            boolean totalNumModulesRequest = (i == indexOfTotalNumModulesPrompt) ? true : false;
            boolean numModulesPerCourseReq = (i == indexOfNumModulesPerCourse) ? true : false;
            int numModulesTotal = 0;
            if (numModulesPerCourseReq) {
                numModulesTotal = hmap.get("M");
            }

            int input = getInputFromUser(scan, promptBeginning + prompts[i], standardErrorMessage, totalNumModulesRequest, numModulesPerCourseReq, numModulesTotal);
            if (input == -1) {
                quit = true;
                hmap = null;
            } else {
                hmap.put(keys[i], input);
            }
        }
        if (hmap != null) {
            int numExamDays = hmap.get("M") / 2;
            hmap.put("D", numExamDays);
        }
        return hmap;
    }


    private static int getInputFromUser(Scanner scan, String prompt, String error, boolean totalNumModulesRequest, boolean numModulesPerCourseReq, int numModulesTotal) {
        String input;
        int result = 0;
        boolean exitLoop = false;
        while(!exitLoop) {
            System.out.println(prompt);
            input = scan.nextLine();
            if (isInteger(input)) {
                int number = Integer.parseInt(input);
                if (number == 0) {
                    System.out.println("Invalid Input. Must be greater than 0\n");
                } else if(totalNumModulesRequest && !isEven(number)) {
                    System.out.println("Invalid Input. 'Total number of modules' must be even\n");
                } else if(numModulesPerCourseReq && (number > numModulesTotal)) {
                    System.out.println("Invalid Input. Number of modules per course must be less than the number of total modules (" + numModulesTotal + ")\n");
                } else {
                    exitLoop = true;
                    result = number;
                }
            } else if (input == null) {
                exitLoop = true;
                result = -1;
            } else {
                System.out.println(error);
            }
        }
        return result;
    }

    public static boolean isInteger(String s) {
        try {
            int x = Integer.parseInt(s);
            if (x < 0) {
                return false;
            }
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    private static int getMaxPopulationSize(int numModules) {
        final int MAX_PERMISSABLE_POPULATION_SIZE = 800;
        int maxPopulationSize = 1;

        for (int i = 1; i <= numModules; i++) {
            maxPopulationSize = (maxPopulationSize * i);
            if (maxPopulationSize <= MAX_PERMISSABLE_POPULATION_SIZE) {
                maxPopulationSize = MAX_PERMISSABLE_POPULATION_SIZE;
                break;
            }
        }
        return maxPopulationSize;
    }

    private static ArrayList<HashSet<Integer>> generateStudentModuleInfo(int numStudents, int numModulesPerCourse, int totalNumberOfModules) {
        ArrayList<HashSet<Integer>> studentModuleInfo = new ArrayList<>();
        for (int i = 0; i < numStudents; i++) {
            HashSet<Integer> studentModules = new HashSet<>();
            for (int j = 0; j < numModulesPerCourse; j++) {
                boolean valid = false;
                while(!valid) {
                    int rand = getRandomNumberInRange(totalNumberOfModules, 1);
                    if (!studentModules.contains(rand)) {
                        valid = true;
                        studentModules.add(rand);
                    }
                }
            }
            studentModuleInfo.add(studentModules);
        }
        return studentModuleInfo;
    }

    private static ArrayList<int[][]> generateOrderings(HashMap<String, Integer> inputs, ArrayList<HashSet<Integer>> studentModuleInfo) {
        ArrayList <int[][]> orderings = new ArrayList<>();
        for (int i = 0; i < inputs.get("P"); i++) {
            boolean valid = false;
            while(!valid) {
                int[][] ordering = createOrdering(inputs.get("D"), inputs.get("M"), studentModuleInfo);
                if (!isOrderingAlreadyInOrderingsList(ordering, orderings)) {
                    orderings.add(ordering);
                    valid = true;
                }
            }
        }
        return orderings;
    }

    private static int[][] createOrdering(int numberOfExamDays, int totalNumberOfModules, ArrayList<HashSet<Integer>> studentModuleInfo) {
        int NUMBER_EXAMS_PER_DAY = 2;
        int fitnessCost = 0;
        int[][] ordering = new int[numberOfExamDays + 1][NUMBER_EXAMS_PER_DAY];
        HashSet<Integer> alreadyScheduledExams = new HashSet<>();

        for (int i = 0; i < numberOfExamDays; i++) {
            for (int j = 0; j < NUMBER_EXAMS_PER_DAY; j++) {
                boolean valid = false;
                while (!valid && alreadyScheduledExams.size() <= totalNumberOfModules) {
                    int rand = getRandomNumberInRange(totalNumberOfModules, 1);
                    if (!alreadyScheduledExams.contains(rand)) {
                        alreadyScheduledExams.add(rand);
                        ordering[i][j] = rand;
                        valid = true;
                    }
                }
            }

            //Calculate fitness cost for ordering while creating it
            int x = calculateFitnessCostForOneExamSession(studentModuleInfo, ordering[i][0], ordering[i][1]);
            fitnessCost+= x;
        }

        ordering[numberOfExamDays][0] = fitnessCost;
        ordering[numberOfExamDays][1] = 0;

        return ordering;
    }

    private static boolean isOrderingAlreadyInOrderingsList(int[][] ordering, ArrayList<int[][]> orderings) {
        for (int i = 0; i < orderings.size(); i++) {
            if (Arrays.deepEquals(orderings.get(i), ordering)) {
                return true;
            }
        }
        return false;
    }

    // To be used in Final Submission

    /*private static void recalculateFitnessCostOfOrderings(ArrayList<int[][]> orderings, ArrayList<HashSet<Integer>> studentModuleInfo) {
        for (int i = 0; i < orderings.size(); i++) {
            fitnessFunction(orderings.get(i), studentModuleInfo);
        }
    }

    private static void fitnessFunction(int[][] ordering, ArrayList<HashSet<Integer>> studentModuleInfo) {
        int cost = 0;
        for (int i = 0; i < ordering.length; i++) {
            int session1 = ordering[i][0];
            int session2 = ordering[i][1];

            cost += calculateFitnessCostForOneExamSession(studentModuleInfo, session1, session2);
        }

        ordering[ordering.length][0] = cost;
    } */

    private static int calculateFitnessCostForOneExamSession(ArrayList<HashSet<Integer>> studentModuleInfo, int exam1, int exam2) {
        int cost = 0;
        for (int studentId = 0; studentId < studentModuleInfo.size(); studentId++) {
            if(studentTakesModule(studentId, exam1, studentModuleInfo) && studentTakesModule(studentId, exam2, studentModuleInfo)) {
                cost++;
            }
        }
        return cost;
    }

    private static boolean studentTakesModule(int studentID, int moduleNumber, ArrayList<HashSet<Integer>> studentModuleInfo) {
        return studentModuleInfo.get(studentID).contains(moduleNumber);
    }

    private static int getRandomNumberInRange(int max, int min) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static boolean isEven(int num) {
        return (num % 2 == 0);
    }

    private static void printStudentModuleInfo(ArrayList<HashSet<Integer>> arr) throws IOException {
        FileWriter fw = new FileWriter("AI17.txt", false);
        for (int i = 0 ; i < arr.size(); i++) {
            fw.write("Student " + (i + 1) + ": ");

            Iterator<Integer> it = arr.get(i).iterator();
            while(it.hasNext()){
                fw.write("M" + it.next() + " ");
            }
            fw.write("\n");

        }
        fw.close();
    }

    private static void printOrderings(ArrayList<int[][]> orderings) throws IOException{
        for (int i = 0; i < orderings.size(); i++) {
            int[][] ordering = orderings.get(i);
            printOrdering(ordering, i);
        }
    }

    private static void printOrdering(int[][] ordering, int index) throws IOException{
        FileWriter fw = new FileWriter("AI17.txt", true);
        fw.write("\nOrd" + (index + 1) + ":\t");
        for (int i = 0; i < ordering.length - 1; i++) {
            String str = String.format("M%-3d  ", ordering[i][0]);
            fw.write(str);
        }

        fw.write(": cost: " + ordering[ordering.length-1][0] + "\n\t\t");

        for (int i = 0; i < ordering.length - 1; i++) {
            String str = String.format("M%-3d  ", ordering[i][1]);
            fw.write(str);
        }
        fw.write("\n");
        fw.close();
    }

    private static void printHashMap (HashMap<String, Integer> hmap) {
        for (String name: hmap.keySet()){
            int value = hmap.get(name);
            System.out.println(name + " " + value);
        }
    }

    private static void printWarningForAdjustedPopulationSize(int original, int max, int totalNumOfModules) throws IOException{
        FileWriter fw = new FileWriter("AI17.txt", true);
        String warningMsg = "\n\n" +
                "User Requested a Population size of " + original + "\n" +
                "Given that the total number of modules is "+ totalNumOfModules + ", it is not possible to have a population size this large\n" +
                "The maximum permissible number of orderings for " + totalNumOfModules + " modules is: " + max + "\n";
        fw.write(warningMsg);
        fw.close();
    }

}