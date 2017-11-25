package org.lmc.nlp.crfpp.predict;

/**
 * CRF predict example
 */
public class CRFTest {

    public static void main(String[] args) {
        CRFModel model = CRFModelFacade.getFromPath("/model-path/model.txt");
//        CRFModel model = CRFModelFacade.getFromResources("model.txt");

        //build feature matrix
        String input = "宝顶山沿革和密宗史实";
        String[][] table = new String[input.length()][2];
        for(int i = 0; i < input.length(); i++) {
            table[i][0] = input.charAt(i) + "";
        }

        Result result = CRFModelFacade.predict(model, table, true);

        for(String t: result.getTags()) {
            System.out.println(t);
        }

        if(result.getScore() != null) {
            double[][] score = result.getScore();
            printScore(score, model, input);
        }

        System.out.println("-------------");

        result = CRFModelFacade.greedyPredict(model, table, true);

        for(String t: result.getTags()) {
            System.out.println(t);
        }

        if(result.getScore() != null) {
            double[][] score = result.getScore();
            printScore(score, model, input);
        }
    }

    private static void printScore(double[][] score, CRFModel model, String input) {
        for(int i = 0; i < score.length; i++) {
            for(int j = 0; j < score[i].length; j++) {
                System.out.print(input.charAt(i) + "-" + model.getIdTag(j) + ":" + score[i][j] + "\t");
            }
            System.out.println("\n");
        }
    }
}
