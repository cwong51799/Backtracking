import java.lang.reflect.Array;
import java.util.*;
/*
12
6
1 2 3 4 5 6
5 6 8 9
1 4 7 10
2 5 7 8 11
3 6 9 12
10 11
 */

/*
31
15
1 2 3
4 5 6 7 8 9 10
1 11 12 13
4 14 15 16 17 18
11 19
5 20
14 21 22 23
15 19 24 25 26
27
6 16 20 24
2 7 25 27 28 29
8 12 21 28 30
13 17 29 30
9 18 22 26 31
3 10 23 31
 */

/*
40
20
1 2 3
4 5 6
7 8 9 10
11 12 13 14
15 16
4 17 18 19 20
7 17 21 22
1 11 23
8 12 15 24 25 26 27
28 29
28 30 31
13 16 18 30 32
5 9 24 33 34 35 36 37
21 33
19 23 34 38 39
2 25
35
10 26 32 38 40
6 14 29 31 36 39
3 20 22 27 37 40
 */

/*
20
35
6 7 8 16 18
3 5 14 19 20
6 8 11 13 17
1 4 11 16 20
2 5 8 12 20
1 9 14 16 17
5 10 11 15 19
2 4 11 14 20
4 11 12 15 17
8 11 12 15 18
7 8 10 11 19
3 7 12 19 20
1 3 8 18 20
8 11 14 16 19
2 6 9 18 19
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
 */
/*
30
50
8 12 15 17 21
8 10 14 22 30
6 8 17 19 27
2 9 11 22 28
7 10 16 17 29
8 9 17 25 29
6 9 11 24 29
1 2 9 12 23
8 11 14 16 27
3 6 11 12 20
4 22 25 26 27
1 2 8 18 24
9 11 15 16 22
7 8 11 16 29
10 11 12 22 28
6 18 19 28 30
18 19 24 28 29
2 13 18 19 29
2 7 8 9 23
15 17 20 24 25
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
Minimum set cover of size 9: [Subset#1, Subset#2, Subset#8, Subset#10, Subset#11, Subset#14, Subset#17, Subset#18, Subset#25]
/*

 */
/* OK.
   This shit has these caveats:
It continues searching.
I reduced the amount of checks by:
    1. Only check possible solutions which are smaller than the current best solution.
    2. Any "partial permutations" of the best solution (all the elements of the best solution - 1 cannot be a better solution)
        ex. Current best solution = {3,4,5}
        {3,4} {3,5} {4,3} {4,5} {5,3} {5,4}
        Are immediately rejected because they can only be completed by the current best solution.
    3. I sort the subsets by number and turn that combination into a string which I then do:
        If (str exists in hashmap){
            rejected bc we already reviewed a different permutation of it
        else{
            add it to the hashmap)
            continue checking
    4. If an answer is being considered as a solution, the amount of distinct elements must = universal set or its rejected
       before further processing.
    5. The subsets array is sorted from largest to smallest before backtrack() is ever called and subsequently when
       candidates are being constructed, they are basically ranked and tested in order of size.
            This is useful because an answer is more likely to be found in a larger set and once found, the further candidates don't need to
            get checked since you'll only be looking for smaller solutions.
    5. Construct candidates doesn't allow subsets of current candidates to be added.
           Actually moved this to before backtracking is even called, the input is now unique sets only.
    6. Construct candidates checks if adding the subset can actually contribute anything, if not, it doesn't add it to the options.
*/

// Don't need to check any partial permutations of the setCover of size k-1
// Can maybe optimize is_a_solution


public class FindMinSetCover {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the input: ");
        int universalSet = Integer.parseInt(input.nextLine());
        int numSubsets = Integer.parseInt(input.nextLine());
        Subset[] subsets = new Subset[numSubsets];
        for (int i=0;i<numSubsets;i++){
            String temp = input.nextLine();
            String[] strSubset = temp.split(" ");
            ArrayList<Integer> newSubset = new ArrayList<Integer>();
            for (int j=0;j<strSubset.length;j++){
                if(!strSubset[j].equals("")) {
                    newSubset.add(Integer.parseInt(strSubset[j]));
                }
            }
            subsets[i]= new Subset(newSubset,i+1);
        }
        // Filtering out non-unique sets
        List<Subset> listThinner = Arrays.asList(subsets);
        ArrayList<Subset> newList = new ArrayList<>();
        for (int i=0;i<listThinner.size();i++){
            // Create a new, more minimal list where subsets aren't overlapped.
            if (!isASubsetOfExistingSubset(newList, listThinner.get(i))){
                newList.add(listThinner.get(i));
            }
        }
        subsets = new Subset[newList.size()];
        for (int i=0;i<newList.size();i++){
            subsets[i] = newList.get(i);
        }

        Subset[] answer = new Subset[numSubsets];
        setCover = new Subset[numSubsets];
        int k = 0; // is the first element of the array correct?
        // Sort subsets by biggest first
        Arrays.sort(subsets, new Comparator<Subset>() {
            @Override
            public int compare(Subset o1, Subset o2) {
                if (o1.getValues().size() > o2.getValues().size()){
                    return -1;
                }
                if (o1.getValues().size() < o2.getValues().size()){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        });
        backtrack(answer, k, subsets, universalSet);
        System.out.println(GREEN + "Minimum set cover of size " + minAmtSubsets +": "+ printSetCover() + RESET);
    }


    static boolean finished = false;
    static int minAmtSubsets = 9999;
    static Subset[] setCover;
    static Map<String, Boolean> discoveredSets = new HashMap<String,Boolean>();


    public static void backtrack(Subset[] answer, int k, Subset[] input, int universalSet){
        // System.out.println(GREEN+"Backtrack called. k="+k+" and answer= "+printArrayToK(answer, k)+RESET);
        if (k >= minAmtSubsets){
            return;
        }
        String sortedStr = sortedBySubsetNum(answer,k);
        if(discoveredSets.containsKey(sortedStr)){
            //System.out.println("This element was already examined");
            return;
        }
        else{
            discoveredSets.put(sortedStr, true);
        }
       /* Is this an edge case? Might not even be worth having. Or maybe make it more conditional so that
        not EVERY one goes through this.*/

        if (isPartialPermutationOfCurrentSetCover(answer, k)){
            //System.out.println(printArrayToK(answer,k+1) + " is a partial permutation of " + printSetCover());
            return;
        }
        ArrayList<Subset> candidates = new ArrayList<Subset>();
        if (is_a_solution(answer,k,input,universalSet)){
            process_solution(answer,k,input);
        }
        else {
            k = k+1;
            construct_candidates(answer,k,input,candidates, universalSet);
            if(candidates.size() == 0) {
                return;
            }
            // System.out.println("Current answer: " + printArrayToK(answer, k));
            // System.out.println("Candidates for " + RED + (k) + RESET + ": " + candidates);
            for (int i=0;i<candidates.size();i++){
                if (k>=minAmtSubsets){
                    // System.out.println("Don't need to check answers >" + minAmtSubsets);
                    return;
                }
                //System.out.println("Trying out " + PURPLE + candidates.get(i) + RESET + " for Spot " + RED + (k) + RESET);
                answer[k-1] = candidates.get(i);
                backtrack(answer, k, input, universalSet);
                if(finished) {
                    return;
                }
            }
        }
    }
    // Add up all the numbers in all the subsets currently in answer and see if it meets the requirement.
    // Can't do this because there can be repeats
    public static boolean is_a_solution(Subset[] answer, int k, Subset[] input, int universalSet){
        int numToMeet = (universalSet * (universalSet+1))/2;
        ArrayList<Integer> distinctNums = new ArrayList<Integer>();
        for (int i=0;i<k;i++){
            for (int j=0;j<answer[i].getValues().size();j++){
                if (!distinctNums.contains(answer[i].getValues().get(j))) {
                    distinctNums.add(answer[i].getValues().get(j));
                }
            }
        }
        int runningSum = 0;
        for (Integer i : distinctNums){
            runningSum += i;
        }
        return (numToMeet == runningSum);
    }
    // Stop the process and print out the answer
    public static void process_solution(Subset[] answer, int k, Subset[] input){
        if (k < minAmtSubsets) {
            minAmtSubsets = k;
            setCover = answer.clone();
            /*System.out.println(GREEN + "Found a new answer of size " + k + " subsets!" + RESET);
            for (int i=0;i<k;i++){
                System.out.println(answer[i] + "" + ":" + answer[i].getValues());
            }*/
        }
    }

    public static void construct_candidates(Subset[] answer, int k, Subset[] input, ArrayList<Subset> candidates, int universalSet){
        // There should be no possible candidates for spots where k>=minAmtFound already
        if (k >= minAmtSubsets){
            return;
        }
        for (int i=0;i<input.length;i++){
            if (!existsByIndexK(answer, k-1, input[i]) && hasSomethingNeeded(answer, k, input[i], universalSet)){
                //System.out.println(input[i] + " does not exist! Adding it candidates now.");
                candidates.add(input[i]);
            }
        }
    }
  /*  public static boolean hasAlreadyChecked(Subset[] answer, int k, Subset subset){
        Subset[] tempAnswer = answer.clone();
        tempAnswer[k] = subset;
        String tempStrAnswer = sortedBySubsetNum(tempAnswer, k);
        if (discoveredSets.containsKey(tempStrAnswer)){
            return true;
        }
        else{
            return false;
        }
    }*/
    public static boolean hasSomethingNeeded(Subset[] answer, int k, Subset subset, int maxValue){
        ArrayList<Integer> neededValues = new ArrayList<>();
       // System.out.println("Has something I needed called");
        //System.out.println(Arrays.toString(answer));
        for (int i=1;i<=maxValue;i++){
            neededValues.add(i);
        }
        for (int i=0;i<k;i++){
            if (answer[i] == null){
                return true; // it's empty
            }
            neededValues.removeAll(answer[i].getValues());
        }
       // System.out.println("k="+k+"  " + printArrayToK(answer, k) + "   Needed values: " + neededValues);
        ArrayList<Integer> neededValuesIfAdded = (ArrayList<Integer>)neededValues.clone();
        neededValuesIfAdded.removeAll(subset.getValues());
        //System.out.println("Needed values if added " + neededValuesIfAdded);
        // Same amount of numbers are needed if removing all numbers in the subset at question
        if (neededValuesIfAdded.size() == neededValues.size()){
         //   System.out.println("Does not need subset " + subset.toString());
            return false; // don't need anything from it
        }
        else{
            return true;
        }
    }

    // If a current subset has all the values in subset and more
    public static boolean isASubsetOfExistingSubset(ArrayList<Subset> candidates, Subset subset){
        ArrayList<Integer> subsetValues = subset.getValues();
        // For each candidate, check if it contains all values of the subset and return true if yes.
        for (int i=0;i<candidates.size();i++){
            if (candidates.get(i).getValues().containsAll(subsetValues)){
                return true;
            }
        }
        return false;
    }
    // [3,0,1,8,9,2] don't include [3,0,1,8,9]
    public static boolean existsByIndexK(Subset[] answer, int k, Subset subsetToFind){
        //System.out.println("Checking if " + subsetToFind + " exists in " + Arrays.toString(answer));
        for (int i=0;i<k;i++){
            if (answer[i] == null){
                return false;
            }
            if (answer[i].equals(subsetToFind)){
                return true;
            }
        }
        return false;
    }
    // Check if the current answer is just a permutation of the current solution. Possibly an edge case?
    // Maybe not worth checking. Thinking about only checking if k=minCoverAmt -1, wait im already doing that
    public static boolean isPartialPermutationOfCurrentSetCover(Subset[] answer, int k){
        boolean isPermutation = true;
        if (minAmtSubsets == 9999){
            return false;
        }
        if (k==minAmtSubsets-1) {
            for (int i = 0; i < k; i++) {
                // If the element inspected is in answer is not found in minCover, it can't be one.
                // Check for existance across the whole array
                if(!existsByIndexK(setCover, minAmtSubsets, answer[i])){
                    isPermutation = false;
                }
            }
        }
        else{
            isPermutation = false;
        }
        return isPermutation;
    }
    public static String printArrayToK(Subset[] arr, int k){
        String str = "[";
        for (int i=0;i<k;i++){
            if (i==k-1){
                str += RED + "_ ";
                break;
            }
            str += arr[i] + ", ";
        }
        str += RESET + "]";
        return str;
    }
    public static String printSetCover(){
        String str = "[";
        if (minAmtSubsets==9999){
            return "There was no set cover found";
        }
        for (int i=0;i<minAmtSubsets;i++){
            if (i != 0 ){
                str +=", ";
            }
            str += setCover[i];
        }
        str += "]";
        return str;
    }
    public static String sortedBySubsetNum(Subset[] arr, int k){
        int[] sorted = new int[k];
        String str = "";
        // Copy the first k elements of the array
        for (int i=0;i<k;i++){
            sorted[i] = arr[i].getNum();
        }
        Arrays.sort(sorted);
        // The array will be the set numbers sorted.
        for (int i=0;i<sorted.length;i++){
            if (i!=0){
                str += ",";
            }
            str += sorted[i];
        }
        // [4,5,3] -> [3,4,5] -> "3,4,5"
        return str;
    }
}

class PossibleAnswer{
    ArrayList<Integer> valuesNeeded = new ArrayList<Integer>();
    int k;
    Subset[] answer;
    public PossibleAnswer(Subset[] answer, int k, int maxSize){
        this.answer = answer;
        this.k = k;
        for (int i=1;i<=maxSize;i++){
            valuesNeeded.add(i);
        }
    }
    public void addToAnswer(Subset subset){
        k++;
        answer[k] = subset;
        for (int i=0;i<subset.getValues().size();i++){
            // I want to remove the value, not the index. The idea is to
            // see if a subset has ANYTHING we need, if not don't even make it a candidate.
            valuesNeeded.remove((Object)subset.getValues().get(i));
        }
    }
    public ArrayList<Integer> getValuesNeeded(){
        return valuesNeeded;
    }
}
class Subset {
    ArrayList<Integer> values;
    int num;

    public Subset(ArrayList<Integer> values, int num) {
        this.values = values;
        this.num = num;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public boolean equals(Subset subset2) {
        boolean equals = true;
        if (subset2.values.size() != this.values.size()) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            if (this.values.get(i) != subset2.getValues().get(i)) {
                equals = false;
            }
        }
        return equals;
    }

    public String toString() {
        //Return values
        //return (""+values);
        // Return simpler form
        return ("Subset#" + num);
    }

    public int getNum() {
        return (num);
    }
}