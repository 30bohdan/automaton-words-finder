
import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;


	

public class AutomatonWordsFinder {  

   

    public static void automatonWordFinder(Automaton testAutomaton, PrintWriter out, int upto) {
        for (int i = 1; i < upto; i++) {
            ResultingSides res = testAutomaton.findGroupEl(i);
            if (res != null) {
                long w = testAutomaton.encoder(res.word, testAutomaton.numOfLetters);
                long g = testAutomaton.encoder(res.groupEl, testAutomaton.numOfStates);
                ResultingSides result = testAutomaton.findImage(res.groupEl, res.word);
                if ((testAutomaton.encoder(result.word, testAutomaton.numOfLetters) == w)
                        && (testAutomaton.encoder(result.groupEl, testAutomaton.numOfStates) == g)) {
                    out.println("Ok");
                } else {
                    out.println("fail");
                }
                out.println("WordLength=" + i);
                out.printf("word = ");
                for (int j = 0; j < res.word.length; j++) {
                    out.printf("%d", res.word[j] + 1);
                }
                out.println();
                out.printf("groupElement = ");
                for (int j = 0; j < res.groupEl.length; j++) {
                    out.printf("%d", res.groupEl[j] + 1);
                }
                out.println();

            } else {
                out.println("no words of length " + i);
            }
        }
    }
    
    public static void genAllRevAutomaton(int numOfStates, int numOfLabels, PrintWriter out) {
        long[] factorials = new long[15];
        factorials[0] = 1;
        for (int j = 1; j < 15; j++) {
            factorials[j] = j * factorials[j - 1];
        }
       
        
        for (long i = 0; i < Math.pow(factorials[numOfStates], numOfLabels); i++) {
            for (long j = 0; j < Math.pow(factorials[numOfLabels], numOfStates); j++) {
                long i1 = i;
                long i2 = j;
                Automaton result = new Automaton(numOfStates, numOfLabels);
                for (int nArrow = 0; nArrow < numOfLabels; nArrow++) {
                    byte[] colomn = factorDecoder(i1 % factorials[numOfStates], numOfStates);
                    for (int t = 0; t < numOfStates; t++) {
                        result.arrows[t][nArrow] = colomn[t];
                    }
                    i1 = i1 / factorials[numOfStates];
                }
                for (int nPer = 0; nPer < numOfStates; nPer++) {
                    byte[] row = factorDecoder(i2 % factorials[numOfLabels], numOfLabels);
                    System.arraycopy(row, 0, result.permutation[nPer], 0, numOfLabels);
                    i2 = i2 / factorials[numOfLabels];
                }

                result.print("Automaton ("+i+", "+j+") ", out);

            }
        }
        

    }

    public static void genCharacterForAllRevAutomaton(int numOfStates, int numOfLabels, PrintWriter out) {
        long[] factorials = new long[15];
        factorials[0] = 1;
        for (int j = 1; j < 15; j++) {
            factorials[j] = j * factorials[j - 1];
        }

        final int wordbound = 10;
        final int groupbound = 10;
        long[][] statistics = new long[wordbound + 1][groupbound + 1];

        for (long i = 0; i < Math.pow(factorials[numOfStates], numOfLabels); i++) {
            for (long j = 0; j < Math.pow(factorials[numOfLabels], numOfStates); j++) {
                long i1 = i;
                long i2 = j;
                Automaton result = new Automaton(numOfStates, numOfLabels);
                for (int nArrow = 0; nArrow < numOfLabels; nArrow++) {
                    byte[] colomn = factorDecoder(i1 % factorials[numOfStates], numOfStates);
                    for (int t = 0; t < numOfStates; t++) {
                        result.arrows[t][nArrow] = colomn[t];
                    }
                    i1 = i1 / factorials[numOfStates];
                }
                for (int nPer = 0; nPer < numOfStates; nPer++) {
                    byte[] row = factorDecoder(i2 % factorials[numOfLabels], numOfLabels);
                    System.arraycopy(row, 0, result.permutation[nPer], 0, numOfLabels);
                    i2 = i2 / factorials[numOfLabels];
                }

                boolean found = false;
                int i10;
                for (i10 = 1; i10 < wordbound; i10++) {
                    ResultingSides res = result.findGroupEl(i10);
                    if (res != null) {
                        found = true;
                        break;
                    }
                }
                int j1;
                for (j1 = 1; j1 < groupbound; j1++) {
                    ResultingSides res = result.dual().findGroupEl(j1);
                    if (res != null) {
                        found = true;
                        break;
                    }
                }

                statistics[i10-1][j1-1]++;

                if (!found) {
                    out.println("found at step " + i + " " + j);
                    out.printf("byte[][] ar1 = {");
                    for (int i5 = 0; i5 < numOfStates; i5++) {
                        out.printf("{");
                        for (int j5 = 0; j5 < numOfLabels - 1; j5++) {
                            out.printf("%d, ", result.arrows[i5][j5]);
                        }
                        out.printf("%d}}; ", result.arrows[i5][numOfLabels - 1]);

                    }
                    out.println();
                    out.printf("byte[][] ar2 = {");
                    for (int i5 = 0; i5 < numOfStates; i5++) {
                        out.printf("{");
                        for (int j5 = 0; j5 < numOfLabels - 1; j5++) {
                            out.printf("%d, ", result.permutation[i5][j5]);
                        }
                        out.printf("%d}}; ", result.permutation[i5][numOfLabels - 1]);

                    }
                    out.println();
                }

            }
        }
        out.println("Number of Automatons for which we have next minimal lengthes");
        for (int t1 = 0; t1 < wordbound; t1++) {
            for (int t2 = 0; t2 < groupbound; t2++) {
                out.printf("%6d ", statistics[t1][t2]);
            }
            out.println();
        }

    }
    
    public static void graphFileCreator(Automaton a, int length, PrintWriter out){
        
        int numWords = (int) Math.pow(a.numOfLetters, length);
            
            out.println("Sourse\tTarget\tLabels");
            for (int step = 0; step < numWords; step++) {
                byte[] goodLetters = new byte[a.numOfStates];
                for (byte i = 0; i < a.numOfStates; i++) {
                    byte[] word = a.decoder(step, a.numOfLetters, length); 
                    ResultingSides image = a.findImage(i, word);
                    if (image.groupEl[0] == i) {
                        // Insertion to print all arrows, that saves letter
                        out.printf("N");
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", word[t]);                            
                        }
                        out.printf("\tN");
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", a.findImage(i, word).word[t]);                            
                        }
                        out.printf("\t"+i);
                        out.println();                                               
                    }
                }               
            }
        ////////////////////////////////////////////////////////////////////////
        out.flush();
        
        
        
    }
    
    public static void fullgraphFileCreator(Automaton a, int length, PrintWriter out){
        
        int numWords = (int) Math.pow(a.numOfLetters, length);
            
            out.println("Sourse\tTarget\tLabels");
            for (int step = 0; step < numWords; step++) {
                
                for (byte i = 0; i < a.numOfStates; i++) {
                    byte[] word = a.decoder(step, a.numOfLetters, length); 
                    ResultingSides image = a.findImage(i, word);
                    
                        // Insertion to print all arrows, that saves letter
                        out.printf("N");
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", word[t]);                            
                        }
                        out.printf("\tN");
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", image.word[t]);                            
                        }
                        out.printf("\t"+i+"|"+image.groupEl[0]);
                        out.println();                                               
                    
                }               
            }
        ////////////////////////////////////////////////////////////////////////
        out.flush();       
        
    }
    
    public static void symGraphFileCreator(Automaton a, int wordLength, int groupLen, PrintWriter out){
        
        int numWords = (int) Math.pow(a.numOfLetters, wordLength);
        int numGWords = (int) Math.pow(a.numOfStates, groupLen);
            
            out.println("Sourse\tTarget\tLabels");
            for (int step = 0; step < numWords; step++) {
                
                for (byte i = 0; i < numGWords; i++) {
                    byte[] word = a.decoder(step, a.numOfLetters, wordLength);
                    byte[] groupEl = a.decoder(i, a.numOfStates, groupLen);
                    ResultingSides image = a.findImage(groupEl, word);
                    
                        // Insertion to print all arrows, that saves letter
                        
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", word[t]);                            
                        }
                        out.printf("|");
                        for (int t=0; t<groupEl.length; t++){                            
                            out.printf("%d", groupEl[t]);                            
                        }
                        out.printf("\t");
                        for (int t=0; t<word.length; t++){                            
                            out.printf("%d", image.word[t]);                            
                        }
                        out.printf("|");
                        for (int t=0; t<groupEl.length; t++){                            
                            out.printf("%d", image.groupEl[t]);                            
                        }
                        out.println();                                               
                    
                }               
            }
        ////////////////////////////////////////////////////////////////////////
        out.flush();       
        
    }
    
    public static void traceFileCreator(Automaton a, ResultingSides init, PrintWriter out){
        ResultingSides step  = init;
        int cipher = a.encoder(step.word, a.numOfLetters);
        out.println("Sourse\tTarget\tLabels");
        do {
            out.printf("N");
            for (int t=0; t<step.word.length; t++){                            
                out.printf("%d", step.word[t]);                            
            }
            ResultingSides image = a.findImage(step.groupEl, step.word);
            out.printf("\tN");
            for (int t=0; t<image.word.length; t++){                            
                 out.printf("%d", image.word[t]);                            
            }
            out.printf("\t"+step.groupEl[0]+"|"+image.groupEl[0]);
            out.println();
            step = image;
        }
        while (a.encoder(step.word, a.numOfLetters)!=cipher);
        out.flush();
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    private class ResSeq {
        byte[] word;
        byte[] grEl;
        
        public ResSeq (byte[] a, byte numOfSt){
           word = new byte[a.length-numOfSt];
           grEl = new byte[numOfSt];
           System.arraycopy(a, 0, word, 0, a.length-numOfSt);
           System.arraycopy(a,(a.length-numOfSt),grEl,0,numOfSt);
        }
        @Override
        public boolean equals(Object ob){
            ResSeq o = (ResSeq) ob; 
            return (Arrays.equals(word, o.word))&&(Arrays.equals(grEl, o.grEl));            
        }
        @Override
        public int hashCode(){            
           return (new Automaton(1,1)).encoder(word, 2);
        }
    }
    
    public void isFree(Automaton aut, byte letter, byte state, byte len){
        for (int t=5; t<len-4; t++){
        HashSet<ResSeq> h = new HashSet(); 
        System.out.println(t);
        
        for (int i=1; i<(1<<len); i++){
            byte[] a = new byte[len];
            byte numOfSt=0;
            
            int cipher = i;
            for (int i1=0; i1<len; i1++){
                byte c =(byte)(cipher%2);
                if (c==0) {
                    a[i1]=letter;
                }
                else {
                    a[i1]=(byte)(aut.numOfLetters+state);
                    numOfSt +=1;
                }
                cipher=cipher/2;
            }
            if (numOfSt == t){
            byte step=0;
            while (a[step]<aut.numOfLetters) step+=1;
            while (step<len){
                if (a[step]<aut.numOfLetters){
                    byte j=step;
                    while ((j>0)&&(a[j-1]>aut.numOfLetters)){
                        byte s =(byte) (a[j-1]-aut.numOfLetters);
                        byte l = a[j];
                        a[j] =(byte) (aut.arrows[s][l]+ aut.numOfLetters);
                        a[j-1] = aut.permutation[s][l];
                        j--;
                    }
                }
                else {
                    step+=1;
                }
            }
            ResSeq r = new ResSeq (a, numOfSt);
            if (h.contains(r)) { 
                System.out.println("Found!");
                for (int i2=0; i2<r.word.length; i2++){
                    System.out.printf("%d", r.word[i2]);
                }
                System.out.printf("|");
                for (int i2=0; i2<r.grEl.length; i2++){
                    System.out.printf("%d", r.grEl[i2]);
                }
                break;
            }
            else{
                h.add(r);
            }
            if (i%50000==0) System.out.println(i);
            }
        }
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    
    private static class CycleFinder{
        PrintWriter out;
        public Automaton a;
        public int length;
        int step =0;
        public byte[] graph;
        public int[] mark;
        ArrayList<Integer> visited = new ArrayList();
        ArrayList<Byte> usedArrows = new ArrayList();
        boolean[] isInCycle;
        TreeSet s = new TreeSet();
        
        private void dfs(int cipher){
            step++; mark[cipher] = step; visited.add(cipher); 
            if (step>1000) System.out.println("alarm!"); 
            byte[] word = a.decoder(cipher, a.numOfLetters, length);
            for (byte i=0; i<a.numOfStates; i++){
                ResultingSides image = a.findImage(i, word);
                if (image.groupEl[0]==i){
                    int imCipher = a.encoder(image.word, a.numOfLetters);
                    
                    if ((mark[imCipher]>0)&&(mark[imCipher]<=step)&&
                            (visited.get(mark[imCipher]-1)==imCipher)){
                        for (int j=mark[imCipher]-1; j<step-1; j++){
                            isInCycle[visited.get(j)]=true;
                            if (!s.contains((Integer) (visited.get(j)*a.numOfStates+usedArrows.get(j)))){
                                byte[] pword = a.decoder(visited.get(j), a.numOfLetters, length);               
                                s.add((Integer)(visited.get(j)*a.numOfStates+usedArrows.get(j)));
                                out.printf("N");
                                for (int i1=0; i1<length; i1++){
                                    out.printf("%d", pword[i1]);
                                }
                                out.printf("\tN");
                                byte[] tword = a.decoder(visited.get(j+1), a.numOfLetters, length);
                                for (int i1=0; i1<length; i1++){
                                    out.printf("%d", tword[i1]);
                                }
                                out.printf("\t");
                                out.println(usedArrows.get(j));
                                out.flush();
                            }
                        }
                        if (!s.contains((Integer) (visited.get(step-1)*a.numOfStates+i))){
                            s.add((Integer)(visited.get(step-1)*a.numOfStates+i));
                            out.printf("N");
                            byte[] pword = a.decoder(visited.get(step-1), a.numOfLetters, length);
                            isInCycle[visited.get(step-1)] = true;
                            for (int i1=0; i1<length; i1++){
                                out.printf("%d", pword[i1]);
                            }
                            out.printf("\tN");
                            byte[] tword = a.decoder(visited.get(mark[imCipher]-1), a.numOfLetters, length);
                            for (int i1=0; i1<length; i1++){
                                out.printf("%d", tword[i1]);
                            }
                            out.printf("\t");
                            out.println(i);
                            out.flush();
                        }
                    }
                    else if ((mark[imCipher]==0)||(isInCycle[imCipher])) {
                        usedArrows.add(i);
                        dfs(imCipher);
                    }
                }
            }
            visited.remove(visited.size()-1);
            if (usedArrows.size()>0) usedArrows.remove(usedArrows.size()-1);
            step--;
        }
        
        public CycleFinder(Automaton a, int length, PrintWriter out){
            out.println("Sourse\tTarget\tLabels");            
            this.a = a; this.out = out;
            this.length = length;
            int n = (int)Math.pow(a.numOfLetters, length);
            graph = new byte[n];
            mark = new int[n];
            
            for (int i=0; i<n; i++){
                isInCycle = new boolean[n];
                //if (mark[i]==0) System.out.println("Computing "+i);
                
                if (mark[i]==0) dfs(i);
                out.flush();
            }
            out.flush();
        }
        
    }
    
    private static class CommutativeWordPrinter{
        PrintWriter out;
        public Automaton a;
        public int length;
        int step =0;
        public byte[] graph;
        public int[] mark;
        ArrayList<Integer> visited = new ArrayList();
        ArrayList<Byte> usedArrows = new ArrayList();
        boolean[] isInCycle;
        TreeSet s = new TreeSet();
        
        private void dfs(int cipher){
            step++; mark[cipher] = step; visited.add(cipher); 
            if (step>1000) System.out.println("alarm!"); 
            byte[] word = a.decoder(cipher, a.numOfLetters, length);
            for (byte i=0; i<a.numOfStates; i++){
                ResultingSides image = a.findImage(i, word);
                if (image.groupEl[0]==i){
                    int imCipher = a.encoder(image.word, a.numOfLetters);
                    
                    if ((mark[imCipher]>0)&&(mark[imCipher]<=step)&&
                            (visited.get(mark[imCipher]-1)==imCipher)){
                        byte[] mword = a.decoder(visited.get(mark[imCipher]-1), a.numOfLetters, length);
                        boolean testB = false;
                        for (int j=mark[imCipher]-1; j<step-1; j++){
                            isInCycle[visited.get(j)]=true;
                            if (!s.contains((Integer) (visited.get(j)*a.numOfStates+usedArrows.get(j)))){
                                byte[] pword = a.decoder(visited.get(j), a.numOfLetters, length);               
                                s.add((Integer)(visited.get(j)*a.numOfStates+usedArrows.get(j)));}

                                byte[] tword = a.decoder(visited.get(j+1), a.numOfLetters, length);
                                
                                for (int sh=1; sh<mword.length; sh++){
                                    boolean b = true;
                                    for (int t=0; t<mword.length; t++){
                                        if (mword[(sh+t)%mword.length]!=tword[t]){
                                           b=false; 
                                        }
                                    }
                                    if (b)  testB=true;                                    
                                }
                                
                            
                            
                        }
                        if (!testB) {
                                    out.printf("N");
                                    for (int i1=0; i1<length; i1++){
                                        out.printf("%d", mword[i1]);
                                    }
                                    out.printf("\tW");
                                    for (int j=mark[imCipher]-1; j<step-1; j++){
                                        out.printf("%d",usedArrows.get(j));
                                    }
                                    out.println();
                        }
                        if (!s.contains((Integer) (visited.get(step-1)*a.numOfStates+i))){
                            s.add((Integer)(visited.get(step-1)*a.numOfStates+i));

                        }
                    }
                    else if ((mark[imCipher]==0)||(isInCycle[imCipher])) {
                        usedArrows.add(i);
                        dfs(imCipher);
                    }
                }
            }
            visited.remove(visited.size()-1);
            if (usedArrows.size()>0) usedArrows.remove(usedArrows.size()-1);
            step--;
        }
        
        public CommutativeWordPrinter(Automaton a, int length, PrintWriter out){
            //out.println("Sourse\tTarget\tLabels");            
            this.a = a; this.out = out;
            this.length = length;
            int n = (int)Math.pow(a.numOfLetters, length);
            graph = new byte[n];
            mark = new int[n];
            
            for (int i=0; i<n; i++){
                isInCycle = new boolean[n];
                //if (mark[i]==0) System.out.println("Computing "+i);
                
                if (mark[i]==0) dfs(i);
                out.flush();
            }
            out.flush();
        }
        
    }
    




	
	private static long factorEncoder(byte[] word) {
        long[] factorials = new long[word.length];
        factorials[0] = 1;
        for (int j = 1; j < word.length; j++) {
            factorials[j] = j * factorials[j - 1];
        }
        long result = 0;
        for (int j = 0; j < word.length; j++) {
            result += word[j] * factorials[word.length - j - 1];
            for (int i = j + 1; i < word.length; i++) {
                if (word[i] > word[j]) {
                    word[i] -= 1;
                }
            }
        }
        return result;
    }

    private static byte[] factorDecoder(long cipher, int length) {
        byte[] result = new byte[length];
        long[] factorials = new long[length];
        factorials[0] = 1;
        for (int j = 1; j < length; j++) {
            factorials[j] = j * factorials[j - 1];
        }
        for (int j = 0; j < length; j++) {
            long t = cipher / factorials[length - 1 - j];
            result[j] = (byte) t;
            cipher = cipher - t * factorials[length - 1 - j];
        }
        for (int j = length - 1; j > -1; j--) {
            for (int i = j+1; i < length; i++) {
                if (result[i] >= result[j]) {
                    result[i] += 1;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws  FileNotFoundException {
        //Scanner in = new Scanner(new File("Input.txt"));
        //PrintWriter out = new PrintWriter(new File("Output.txt"));
        // states would be marked by numbers 0, 1, 2, 3...
        // arrow marks also would be denoted by 0, 1, 2, 3...
        /*
        Automaton testAutomaton = new Automaton(3, 2);
         byte[][] ar1 = {{1, 1}, {0, 2}, {2, 0}};
         byte[][] ar2 = {{0, 1}, {1, 0}, {1, 0}};
         testAutomaton.arrows = ar1;
         testAutomaton.permutation = ar2;
        
         ResultingSides res = testAutomaton.findGroupEl(6);
         out.println(testAutomaton.encoder(res.word, 2)+" "+testAutomaton.encoder(res.groupEl, 3));
                

         for (int i = 1; i < 16; i++) {
         for (int j = 1; j < 10; j++) {
         for (int w = 0; w < Math.pow(2, i); w++) {
         for (int g = 0; g < Math.pow(3, j); g++) {
         byte[] word = testAutomaton.decoder(w, 2, i);
         byte[] groupEl = testAutomaton.decoder(g, 3, j);
         ResultingSides result;
         result = testAutomaton.findImage(groupEl, word);
         if ((testAutomaton.encoder(result.word, 2)==w)&&
         (testAutomaton.encoder(result.groupEl, 3)==g)){
         out.printf("WordLength=" + i);
         out.printf(" word = ");
         for (int j1 = 0; j1 < result.word.length; j1++) {
         out.printf("%d", result.word[j1] );
         }
         out.println();
         out.printf("groupElement = ");
         for (int j1 = 0; j1 < result.groupEl.length; j1++) {
         out.printf("%d", result.groupEl[j1] );
         }
         out.println();

         
         
         }
         }
         }
         }
         }
    */     
        /*    Automaton testAutomaton = new Automaton(4, 3);
         //  [ [ [ 1, 3, 3, (1,3) ], [ 2, 1, 1, (1,3,2) ], [ 3, 4, 4, (1,3,2) ], [ 4, 2, 2, (1,3,2) ] ] ]
         byte[][] ar1 = {{0, 2, 2}, {1, 0, 0}, {2, 3, 3}, {3, 1, 1}};
         byte[][] ar2 = {{2, 1, 0}, {2, 0, 1}, {2, 0, 1}, {2, 0, 1}};
         //  [ [ [ 4, 3, 3, (1,3) ], [ 2, 1, 1, (1,3) ], [ 3, 4, 4, (1,3,2) ], [ 1, 2, 2, (1,3,2) ] ] ]
         byte[][] ar3 = {{3, 2, 2}, {1, 0, 0}, {2, 3, 3}, {0, 1, 1}};
         byte[][] ar4 = {{2, 1, 0}, {2, 1, 0}, {2, 0, 1}, {2, 0, 1}};
         //[ [ [ 2, 2, 2, () ], [ 4, 4, 4, () ], [ 3, 1, 1, (1,3) ], [ 1, 3, 3, (1,3,2) ] ] ]
         byte[][] ar5 = {{1, 1, 1}, {3, 3, 3}, {2, 0, 0}, {0, 2, 2}};
         byte[][] ar6 = {{0, 1, 2}, {0, 1, 2}, {2, 1, 0}, {2, 0, 1}};
         //[ [ [ 2, 2, 2, () ], [ 4, 4, 4, () ], [ 3, 1, 1, (1,2,3) ], [ 1, 3, 3, (1,2) ] ] ],
         byte[][] ar7 = {{1, 1, 1}, {3, 3, 3}, {2, 0, 0}, {0, 2, 2}};
         byte[][] ar8 = {{0, 1, 2}, {0, 1, 2}, {1, 2, 0}, {1, 0, 2}};
         // [ [ [ 3, 3, 3, () ], [ 1, 1, 4, (1,3) ], [ 4, 4, 1, (1,2,3) ], [ 2, 2, 2, () ] ] ]
         byte[][] ar9 = {{2, 2, 2}, {0, 0, 3}, {3, 3, 0}, {1, 1, 1}};
         byte[][] ar10 = {{0, 1, 2}, {2, 1, 0}, {1, 2, 0}, {0, 1, 2}};
         //[ [ [ 3, 3, 3, (1,3) ], [ 1, 1, 4, (2,3) ], [ 4, 4, 1, (1,3,2) ], [ 2, 2, 2, (1,3) ] ] ],
         byte[][] ar11 = {{2, 2, 2}, {0, 0, 3}, {3, 3, 0}, {1, 1, 1}};
         byte[][] ar12 = {{2, 1, 0}, {0, 2, 1}, {2, 0, 1}, {2, 1, 0}};
         // [ [ [ 3, 3, 3, (1,2) ], [ 1, 1, 4, (1,3) ], [ 4, 4, 1, (1,2,3) ], [ 2, 2, 2, (1,2) ] ] ] ;
         byte[][] ar13 = {{2, 2, 2}, {0, 0, 3}, {3, 3, 0}, {1, 1, 1}};
         byte[][] ar14 = {{1, 0, 2}, {2, 1, 0}, {1, 2, 0}, {1, 0, 2}};
         //testAutomaton.arrows = ar1;
         //testAutomaton.permutation = ar2;
         //testAutomaton.arrows = ar3;
         //testAutomaton.permutation = ar4;
         byte[][] testar1 = {{2, 3, 0}, {3, 1, 1}, {1, 2, 2}, {0, 0, 3}};
         byte[][] testar2 = {{2, 0, 1}, {2, 0, 1}, {2, 0, 1}, {0, 2, 1}};

         testAutomaton.arrows = testar1;
         testAutomaton.permutation = testar2;
         testAutomaton = genRandomRevAutomaton(4,3);
         for (int i = 1; i < 14; i++) {
         ResultingSides res = testAutomaton.findGroupEl(i);
         if (res != null) {
         //out.println(testAutomaton.encoder(res.word, 3) + " " + testAutomaton.encoder(res.groupEl, 4));
         long w = testAutomaton.encoder(res.word, 3);
         long g = testAutomaton.encoder(res.groupEl, 4);
         ResultingSides result = testAutomaton.findImage(res.groupEl, res.word);
         if ((testAutomaton.encoder(result.word, 3)==w)&&
         (testAutomaton.encoder(result.groupEl, 4)==g)){
         out.println("Ok");
         }
         else {
         out.println("fail");
         }
         out.println("WordLength=" + i);
         out.printf("word = ");
         for (int j = 0; j < res.word.length; j++) {
         out.printf("%d", res.word[j] + 1);
         }
         out.println();
         out.printf("groupElement = ");
         for (int j = 0; j < res.groupEl.length; j++) {
         out.printf("%d", res.groupEl[j] + 1);
         }
         out.println();

         } else {
         out.println("no words for length " + i);
         }
         }
         */
        /*    for (int count = 1; count < 3000000; count++) {
         int numS = 4;
         int numL = 3;
         Automaton testAutomaton = genRandomRevAutomaton(numS, numL);

         boolean found = false;
         int i1;
         for (i1 = 1; i1 < 8; i1++) {
         ResultingSides res = testAutomaton.findGroupEl(i1);
         if (res != null) {
         found = true;
         break;
         }
         }
         int j1;
         for (j1 = 1; j1<6; j1++){
         ResultingSides res = testAutomaton.dual().findGroupEl(j1);
         if (res != null) {
         found = true;
         break;
         }
         }

         if (!found) {
         out.println("found at step " + count);
         for (int i = 0; i < numS; i++) {
         out.printf("{");
         for (int j = 0; j < numL - 1; j++) {
         out.printf("%d, ", testAutomaton.arrows[i][j]);
         }
         out.printf("%d}, ", testAutomaton.arrows[i][numL - 1]);

         }
         out.println();
         for (int i = 0; i < numS; i++) {
         out.printf("{");
         for (int j = 0; j < numL - 1; j++) {
         out.printf("%d, ", testAutomaton.permutation[i][j]);
         }
         out.printf("%d}, ", testAutomaton.permutation[i][numL - 1]);

         }
         out.println();
         }

         if (count%100==0) System.out.println(count + " " + i1+" "+j1);
         }
         */
        /*    Automaton testAutomaton = new Automaton(4, 3);
         byte[][] ar13 = {{2, 2, 2}, {0, 0, 3}, {3, 3, 0}, {1, 1, 1}};
         byte[][] ar14 = {{1, 0, 2}, {2, 1, 0}, {1, 2, 0}, {1, 0, 2}};

         testAutomaton.arrows = ar13;
         testAutomaton.permutation = ar14;
         automatonWordFinder(testAutomaton, out, 14);
         out.println("dual");
         automatonWordFinder(testAutomaton.dual(), out, 10);
         */
        //genAllRevAutomaton(5, 2, out);
        // ALoshin automaton
        /*Automaton testAutomaton = new Automaton(3, 2);
         byte[][] ar1 = {{1, 1}, {0, 2}, {2, 0}};
         byte[][] ar2 = {{0, 1}, {1, 0}, {1, 0}};
         testAutomaton.arrows = ar1;
         testAutomaton.permutation = ar2;
         automatonWordFinder(testAutomaton, out, 7);
         out.println("dual");
         automatonWordFinder(testAutomaton.dual(), out, 7);
        */ 
        Automaton aloshin = new Automaton(3, 2);
        byte[][] al1 = {{1, 1}, {0, 2}, {2, 0}};
        byte[][] al2 = {{0, 1}, {1, 0}, {1, 0}};
        aloshin.arrows = al1;
        aloshin.permutation = al2;
        ResultingSides sides = new ResultingSides(3, 1);
        byte[] s1 = {0,1,2};
        byte[] s2 = {1};
        sides.word = s1;
        sides.groupEl = s2; 
        
        
        Automaton autC3 = new Automaton(3, 3);
        byte[][] a15 = {{0, 1, 2}, {2,0,1}, {1,2,0}};
        byte[][] a16 = {{0, 2, 1}, {2,1,0}, {1,0,2}};
        autC3.arrows = a15;
        autC3.permutation = a16;
        //PrintWriter out = new PrintWriter("autC3cycles_8.txt");
        new CycleFinder(autC3, 5, new PrintWriter(new File("autC3CycleGr_5.xls")));
        
        
    }
}


