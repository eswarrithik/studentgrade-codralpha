import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Student {
    String name;
    ArrayList<Double> marks;
    double average;
    String grade;

    Student(String name, ArrayList<Double> marks) {
        this.name = name;
        this.marks = marks;
        calculateAverage();
        assignGrade();
    }

    void calculateAverage() {
        average = marks.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    void assignGrade() {
        if (average >= 90) grade = "A";
        else if (average >= 80) grade = "B";
        else if (average >= 70) grade = "C";
        else if (average >= 60) grade = "D";
        else grade = "Fail";
    }
}

class Subject {
    String name, code;

    Subject(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String toString() {
        return name + " (" + code + ")";
    }
}

public class StudentGradeTracker extends JFrame {
    private int totalStudents, studentCount = 0;
    private ArrayList<Subject> subjects = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<JTextField> subjectFields = new ArrayList<>();

    private JTextField nameField;
    private JPanel marksPanel;
    private JTextArea outputArea;
    private JButton submitButton;

    private Student topper = null, lowest = null;

    public StudentGradeTracker() {
        setTitle("📘 Student Grade Tracker");
        setSize(700, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeInputs();
        setupUI();

        setVisible(true);
    }

    private void initializeInputs() {
        try {
            totalStudents = Integer.parseInt(prompt("Enter total number of students:"));
            int totalSubjects = Integer.parseInt(prompt("Enter number of subjects:"));

            for (int i = 1; i <= totalSubjects; i++) {
                String name = prompt("Enter name of Subject " + i + ":");
                String code = prompt("Enter code for " + name + ":");
                subjects.add(new Subject(name, code));
            }
        } catch (NumberFormatException e) {
            showError("Invalid input. Exiting...");
            System.exit(0);
        }
    }

    private void setupUI() {
        // Top Section: Student Info
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        nameField = new JTextField();
        topPanel.add(new JLabel("Student Name:"));
        topPanel.add(nameField);
        add(topPanel, BorderLayout.NORTH);

        // Center Section: Subject Marks
        marksPanel = new JPanel();
        marksPanel.setLayout(new GridLayout(subjects.size(), 2, 5, 5));
        marksPanel.setBorder(BorderFactory.createTitledBorder("Enter Marks"));

        for (Subject subject : subjects) {
            marksPanel.add(new JLabel(subject.toString()));
            JTextField markField = new JTextField();
            subjectFields.add(markField);
            marksPanel.add(markField);
        }
        add(marksPanel, BorderLayout.CENTER);

        // Bottom Section: Output + Submit
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        submitButton = new JButton("Submit");
        outputArea = new JTextArea(12, 30);
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createTitledBorder("Report"));

        submitButton.addActionListener(e -> handleSubmission());

        bottomPanel.add(submitButton, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleSubmission() {
        String name = nameField.getText().trim();
        if (!name.matches("[a-zA-Z ]+")) {
            showError("❌ Name must contain only alphabets!");
            return;
        }

        ArrayList<Double> marks = new ArrayList<>();
        for (JTextField field : subjectFields) {
            try {
                double mark = Double.parseDouble(field.getText().trim());
                if (mark < 0 || mark > 100) throw new NumberFormatException();
                marks.add(mark);
            } catch (NumberFormatException e) {
                showError("❌ Please enter valid marks (0-100) for all subjects.");
                return;
            }
        }

        Student student = new Student(name, marks);
        students.add(student);

        if (topper == null || student.average > topper.average) topper = student;
        if (lowest == null || student.average < lowest.average) lowest = student;

        studentCount++;
        if (studentCount == totalStudents) {
            displayFinalReport();
            submitButton.setEnabled(false);
        } else {
            clearFields();
        }
    }

    private void displayFinalReport() {
        StringBuilder sb = new StringBuilder("🎓 Final Student Report:\n");
        for (Student s : students) {
            sb.append(String.format("%s - Avg: %.2f, Grade: %s\n", s.name, s.average, s.grade));
        }

        sb.append("\n🏆 Topper: ")
          .append(String.format("%s (%.2f - %s)", topper.name, topper.average, topper.grade));

        sb.append("\n📉 Lowest Scorer: ")
          .append(String.format("%s (%.2f - %s)", lowest.name, lowest.average, lowest.grade));

        outputArea.setText(sb.toString());
    }

    private void clearFields() {
        nameField.setText("");
        for (JTextField field : subjectFields) field.setText("");
    }

    private String prompt(String msg) {
        String input = JOptionPane.showInputDialog(this, msg);
        if (input == null || input.trim().isEmpty()) {
            showError("Input cancelled. Exiting...");
            System.exit(0);
        }
        return input.trim();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGradeTracker::new);
    }
}
