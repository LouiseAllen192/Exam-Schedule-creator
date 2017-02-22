import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Scheduler {
    public static void main(String[] args){

        HashMap<String, Integer> inputs, timetable;
        ArrayList<HashMap<String,Integer>> orderings;

        inputs = requestInputs();
        if (inputs != null) {
            timetable = generateStudentsTimeTable(inputs.get("S"), inputs.get("C"), inputs.get("M"));
            orderings = generateOrderings(inputs, timetable);

            for (int i = 0; i < orderings.size(); i++) {
                printHmapContents(orderings.get(i));
                System.out.println("\n\n");
            }
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

    private static HashMap<String,Integer> generateStudentsTimeTable(int numStudents, int numModulesPerCourse, int totalNumberOfModules) {
        HashMap<String, Integer> timetable = new HashMap<String,Integer>();
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numModulesPerCourse; j++) {
                int rand =  getRandomNumberInRange(totalNumberOfModules, 1);
                String key = "S" + (i + 1) + "M" + (j + 1);
                timetable.put(key, rand);
            }
        }
        return timetable;
    }

    private static ArrayList<HashMap<String,Integer>> generateOrderings(HashMap<String, Integer> inputs, HashMap<String, Integer> timetable) {
        ArrayList <HashMap<String,Integer>> orderings = new ArrayList<HashMap<String,Integer>>();
        for (int i = 0; i < inputs.get("P"); i++) {
            HashMap<String,Integer> ordering = createOrdering(inputs.get("D"), inputs.get("M"));
            orderings.add(ordering);
        }
        return orderings;
    }

    private static HashMap<String, Integer> createOrdering(int numberOfExamDays, int totalNumberOfModules) {
        int numberOfExamsPerDay = 2;
        HashMap<String, Integer> ordering = new HashMap<String, Integer>();
        ArrayList<Integer> alreadyScheduledExams = new ArrayList<>();
        for (int i = 0; i < numberOfExamDays; i++) {
            String dayCode = "Day" + (i + 1);
            for(int j = 0; j < numberOfExamsPerDay; j++) {
                String sessionCode = "Ses" + (j + 1);
                String key = dayCode + sessionCode;

                boolean valid = false;
                while (!valid && alreadyScheduledExams.size() <= totalNumberOfModules) {
                    int rand = getRandomNumberInRange(totalNumberOfModules + 1, 1);
                    if (!alreadyScheduledExams.contains(rand)) {
                        alreadyScheduledExams.add(rand);
                        ordering.put(key, rand);
                        valid = true;
                    }
                }

            }
        }

        return ordering;
    }

    private static int getRandomNumberInRange(int max, int min) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private static void printHmapContents(HashMap<String, Integer> hmap) {
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mEntry = (Map.Entry)iterator.next();
            System.out.println("key: "+ mEntry.getKey() + "| Value: " + mEntry.getValue());
        }

    }

    class IntPair {
        final int x;
        final int y;

        IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}


