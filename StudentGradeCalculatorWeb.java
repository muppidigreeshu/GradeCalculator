import fi.iki.elonen.NanoHTTPD;
import java.util.Map;
import java.util.List;
import java.io.IOException;

public class StudentGradeCalculatorWeb extends NanoHTTPD {

    public StudentGradeCalculatorWeb() throws IOException {
        super(8081);  // Use a port different from 8080
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started on http://localhost:8081/");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><head><style>"
                   + "body { font-family: Arial, sans-serif; background-color: #e0f7fa; color: #333; text-align: center; padding: 20px; }"
                   + "h1 { color: #00796b; }"
                   + "form { margin: 0 auto; width: 300px; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                   + "label { font-size: 16px; display: block; margin: 10px 0 5px; }"
                   + "input[type='text'], input[type='submit'] { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }"
                   + "input[type='submit'] { background-color: #00796b; color: white; border: none; cursor: pointer; }"
                   + "input[type='submit']:hover { background-color: #004d40; }"
                   + "p { font-size: 18px; font-weight: bold; }"
                   + "</style></head><body>";
        
        msg += "<h1>Student Grade Calculator</h1>";

        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("numSubjects") != null) {
            int numSubjects = Integer.parseInt(parameters.get("numSubjects").get(0));
            int totalMarks = 0;

            for (int i = 0; i < numSubjects; i++) {
                String subjectKey = "subject" + (i + 1);
                if (parameters.get(subjectKey) != null) {
                    int marks = Integer.parseInt(parameters.get(subjectKey).get(0));
                    totalMarks += marks;
                }
            }

            double averagePercentage = (double) totalMarks / numSubjects;
            String grade = calculateGrade(averagePercentage);

            msg += "<p>Total Marks: " + totalMarks + " out of " + (numSubjects * 100) + "</p>";
            msg += String.format("<p>Average Percentage: %.2f%%</p>", averagePercentage);
            msg += "<p>Grade: " + grade + "</p>";
        } else {
            msg += "<form action='?' method='get'>";
            msg += "<label for='numSubjects'>Number of Subjects:</label>";
            msg += "<input type='text' id='numSubjects' name='numSubjects'><br>";

            msg += "<input type='submit' value='Submit'>";
            msg += "</form>";
        }

        // If number of subjects is entered, show input fields for marks
        if (parameters.get("numSubjects") != null) {
            int numSubjects = Integer.parseInt(parameters.get("numSubjects").get(0));
            msg += "<form action='?' method='get'>";
            msg += "<input type='hidden' name='numSubjects' value='" + numSubjects + "'>";
            for (int i = 0; i < numSubjects; i++) {
                msg += "<label for='subject" + (i + 1) + "'>Marks for Subject " + (i + 1) + ":</label>";
                msg += "<input type='text' id='subject" + (i + 1) + "' name='subject" + (i + 1) + "'><br>";
            }
            msg += "<input type='submit' value='Calculate'>";
            msg += "</form>";
        }

        msg += "</body></html>";
        return newFixedLengthResponse(msg);
    }

    private String calculateGrade(double averagePercentage) {
        if (averagePercentage >= 90) {
            return "A";
        } else if (averagePercentage >= 80) {
            return "B";
        } else if (averagePercentage >= 70) {
            return "C";
        } else if (averagePercentage >= 60) {
            return "D";
        } else if (averagePercentage >= 50) {
            return "E";
        } else {
            return "F";
        }
    }

    public static void main(String[] args) {
        try {
            new StudentGradeCalculatorWeb();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}
