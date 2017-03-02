import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Scheduler {
    public static void main(String[] args){

        HashMap<String, Integer> inputs;
        ArrayList<HashSet<Integer>> studentModuleInfo;
        ArrayList<int[][]> orderings;
        ArrayList<Integer> fitnessCostsPerOrdering;

        inputs = requestInputs();
        if (inputs != null) {
            studentModuleInfo = generateStudentModuleInfo(inputs.get("S"), inputs.get("C"), inputs.get("M"));
            printStudentModuleInfo(studentModuleInfo);

            orderings = generateOrderings(inputs);

            fitnessCostsPerOrdering = calculateFitnessCostOfOrderings(orderings, studentModuleInfo);
            printOrderings(orderings, fitnessCostsPerOrdering);
        }
    }

    private static HashMap<String, Integer> requestInputs() {
        HashMap<String, Integer> hmap = new HashMap<String,Integer>();
        String promptBeginning = "Please enter the ";
        String standardErrorMessage = "Invalid input. Please enter an integer";

        boolean quit = false;
        String[] keys = {"G", "P", "S", "M", "C"};
        String[] prompts = {"number of generations", "population size", "number of students", "total number of modules", "the number of modules per course"};

        for (int i = 0; i < prompts.length && !quit; i++) {
            int input = getInputFromUser(promptBeginning + prompts[i], standardErrorMessage);
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

    private static int getInputFromUser(String prompt, String error) {
        String input;
        int result = 0;
        boolean exitLoop = false;
        while(!exitLoop) {
            input = JOptionPane.showInputDialog(null, prompt);
            if (isInteger(input)) {
                exitLoop = true;
                result = Integer.parseInt(input);
            } else if (input == null) {
                exitLoop = true;
                result = -1;
            } else {
                JOptionPane.showMessageDialog(null, error);
            }
        }
        return result;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
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

    private static ArrayList<int[][]> generateOrderings(HashMap<String, Integer> inputs) {
        ArrayList <int[][]> orderings = new ArrayList<>();
        for (int i = 0; i < inputs.get("P"); i++) {
            boolean valid = false;
            while(!valid) {
                int[][] ordering = createOrdering(inputs.get("D"), inputs.get("M"));
                if (!isOrderingAlreadyInOrderingsList(ordering, orderings)) {
                    orderings.add(ordering);
                    valid = true;
                }
            }
        }
        return orderings;
    }

    private static int[][] createOrdering(int numberOfExamDays, int totalNumberOfModules) {
        int numberOfExamsPerDay = 2;
        int[][] ordering = new int[numberOfExamDays][numberOfExamsPerDay];

        HashSet<Integer> alreadyScheduledExams = new HashSet<>();
        for (int i = 0; i < numberOfExamDays; i++) {
            for(int j = 0; j < numberOfExamsPerDay; j++) {
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
        }
        return ordering;
    }

    private static boolean isOrderingAlreadyInOrderingsList(int[][] ordering, ArrayList<int[][]> orderings) {
        for (int i = 0; i < orderings.size(); i++) {
            if (Arrays.deepEquals(ordering, orderings.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<Integer> calculateFitnessCostOfOrderings(ArrayList<int[][]> orderings, ArrayList<HashSet<Integer>> studentModuleInfo) {
        ArrayList<Integer> fitnessCosts = new ArrayList<>();
        for (int i = 0; i < orderings.size(); i++) {
            fitnessCosts.add(fitnessFunction(orderings.get(i), studentModuleInfo));
        }
        return fitnessCosts;
    }

    private static int fitnessFunction(int[][] ordering, ArrayList<HashSet<Integer>> studentModuleInfo) {
        int cost = 0;
        for (int i = 0; i < ordering.length; i++) {
            int session1 = ordering[i][0];
            int session2 = ordering[i][1];

            for (int studentId = 0; studentId < studentModuleInfo.size(); studentId++) {
                if (studentModuleInfo.get(studentId).contains(session1) && studentModuleInfo.get(studentId).contains(session2)) {
                    cost++;
                }
            }
        }
        return cost;
    }

    private static int getRandomNumberInRange(int max, int min) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static void printHmapContents(HashMap<String, Integer> hmap) {
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mEntry = (Map.Entry)iterator.next();
            System.out.println("key: "+ mEntry.getKey() + "| Value: " + mEntry.getValue());
        }

    }

    private static void printStudentModuleInfo(ArrayList<HashSet<Integer>> arr) {
        for(int i = 0 ; i < arr.size(); i++) {
                System.out.println(arr.get(i));
        }
    }

    private static void printOrderings(ArrayList<int[][]> ordering, ArrayList<Integer> fitnessCostsPerOrdering) {
        for (int i = 0; i < ordering.size(); i++) {
            int[][] x = ordering.get(i);

            for (int j = 0; j < x.length; j++) {
                for(int k = 0; k < x[j].length; k++) {
                    System.out.print(x[j][k] + " ");
                }
                System.out.println();
            }
            System.out.println("Fitness Cost: " + fitnessCostsPerOrdering.get(i));
            System.out.println();
            System.out.println();
        }
    }

}


