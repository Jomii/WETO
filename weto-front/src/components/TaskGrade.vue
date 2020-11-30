<template>
  <div>
    <div v-if="autoGrading">
      <v-simple-table>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">
                Test #
              </th>
              <th class="text-left">
                Score
              </th>
              <th class="text-left">
                Time
              </th>
              <th class="text-left">
                Feedback
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in grades" :key="item.testNo">
              <td>{{ item.testNo }}</td>
              <td>
                <v-chip :color="autoGradeColor(item.testScore)">
                  {{ item.testScore }}
                </v-chip>
              </td>
              <td>{{ item.processingTime }}</td>
              <td>
                <v-chip :color="autoGradeColor(item.testScore)">
                  {{ item.feedback }}
                </v-chip>
              </td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
    </div>
    <div v-else>
      <v-data-table
        :headers="gradeHeaders"
        :items="grades"
        :expanded.sync="expanded"
        item-key="id"
        show-expand
        class="table-header"
        disable-sort
        hide-default-footer
      >
        <template v-slot:top class="table-header">
          <v-toolbar flat>
            <v-toolbar-title><header>Grade</header></v-toolbar-title>
            <v-spacer></v-spacer>
            <v-chip
              >Rating range:
              <v-avatar class="grey darken-1" right>
                <strong>{{ minScore }}</strong>
              </v-avatar>
              <v-avatar right>
                <strong>-</strong>
              </v-avatar>
              <v-avatar class="grey darken-1" right>
                <strong>{{ maxScore }}</strong>
              </v-avatar>
            </v-chip>
          </v-toolbar>
        </template>
        <template v-slot:item.rating="{ item }">
          <v-chip :color="gradeGetColor(item.rating)">
            {{ item.rating }} {{ item.validity }}
          </v-chip>
        </template>
        <template v-slot:expanded-item="{ headers, item }">
          <td :colspan="4">
            <br />
            <v-alert text border="left" colored-border color="blue" type="info">
              <strong>Review text:</strong>
              This is a review text. Lorem ipsum.
            </v-alert>
          </td>
        </template>
      </v-data-table>
      <br />
      <div v-if="!resultsPeriodActive">
        <v-alert border="left" text colored-border type="info" color="blue">
          Results available: <strong>{{ resultsPeriod[0] }}</strong>
        </v-alert>
      </div>
      <div v-else>
        <v-alert
          v-if="overallGrade.mark >= minScore"
          border="left"
          text
          colored-border
          type="success"
          color="green"
        >
          Overall score: <strong>{{ overallGrade.mark }}</strong>
        </v-alert>
        <v-alert
          v-else
          border="left"
          text
          colored-border
          type="error"
          color="red"
        >
          Overall score: <strong>{{ overallGrade.mark }}</strong>
        </v-alert>
      </div>
    </div>
  </div>
</template>

<script>
import api from "../backend-api";

export default {
  name: "taskGrade",
  created() {
    this.checkAutograding();
  },
  computed: {
    status() {
      return this.$store.getters.status;
    },
    taskId() {
      return this.$store.getters.currentTask;
    },
    dbId() {
      return this.$store.getters.selectedCourse.databaseId;
    },
    tabId() {
      return 3;
    },
    taskName() {
      return this.$store.getters.selectedCourse.name;
    },
    user() {
      return this.$store.getters.user;
    },
    subTasks() {
      return this.$store.getters.subTasks;
    },
  },
  data() {
    return {
      autoGrading: false,
      autoGradingResults: [],
      expanded: [],
      minScore: 0,
      maxScore: 0,
      overallGrade: {
        mark: 0,
        timeStampString: "",
      },
      resultsPeriod: [],
      resultsPeriodActive: false,
      grades: [],
      gradeHeaders: [
        {
          text: "Reviewer",
          align: "start",
          sortable: false,
          value: "name",
        },
        { text: "Time", sortable: false, value: "time" },
        { text: "Rating", sortable: false, value: "rating" },
        { text: "", sortable: false, value: "data-table-expand" },
      ],
    };
  },
  methods: {
    getStudentLeafGrades() {
      api
        .getJSONStudentLeafGrades(this.dbId, this.taskId, this.tabId)
        .then((response) => {
          let receivedGrades =
            response.data.studentsGradesMap[this.user.idData.value];
          let resultsPeriodActive = response.data.resultsPeriodActive;
          let visibleMembersMap = response.data.visibleMembersMap;
          // let receivedReviewsMap = response.data.receivedReviewsMap;

          this.resultsPeriod = response.data.resultsPeriod;
          this.resultsPeriodActive = response.data.resultsPeriodActive;

          // Parse min & max score from scoring properties
          let min = 0;
          let max = 0;

          let scoringProperties = response.data.scoring.properties;
          let scoringProps = scoringProperties.split("\n");

          for (let prop of scoringProps) {
            if (prop.includes("minScore")) {
              min = prop.split("=")[1];
            }
            if (prop.includes("maxScore")) {
              max = prop.split("=")[1];
            }
          }

          this.minScore = parseFloat(min);
          this.maxScore = parseFloat(max);

          if (!receivedGrades || !resultsPeriodActive) return;

          Object.entries(receivedGrades).forEach(([key, value]) => {
            // Get overall grade from first object
            if (key === "0") {
              this.overallGrade.mark = value.mark;
              this.overallGrade.timeStampString = value.timeStampString;
            } else {
              // Get all received grades from peer reviews
              let reviewer = value.reviewerId
                ? visibleMembersMap[value.reviewerId]
                : null;
              let gradeObj = {
                id: value.id,
                name: reviewer
                  ? `${reviewer.lastName} ${reviewer.firstName}`
                  : "Anonymous",
                time: value.timeStampString,
                rating: value.mark,
                // text: receivedReviewsMap[value.id] ? receivedReviewsMap[value.id].allTexts : ""
              };

              this.grades.push(gradeObj);
            }
          });
        });
    },
    checkAutograding() {
      api.getSubmissions(this.dbId, this.taskId, 4).then((response) => {
        this.autoGrading = response.data.hasAutoGrading;
        if (response.data.hasAutoGrading) {
          this.getAutoGradingScores();
        } else {
          this.getStudentLeafGrades();
        }
      });
    },
    getAutoGradingScores() {
      api
        .getJSONAutoGrading(
          this.db,
          this.taskId,
          this.taskId,
          this.submissionId
        )
        .then((response) => {
          this.autoGradingResults = response.testScores;
        });
    },
    downloadSubmissionFile(filename, id) {
      api.downloadSubmissionFile(
        filename,
        id,
        this.dbId,
        this.taskId,
        this.tabId
      );
    },
    gradeGetColor(rating) {
      if (rating >= this.maxScore) return "#7dcdbe";
      else if (rating >= this.minScore) return "#ffdca5";
      else return "#f07387";
    },
    autoGradeColor(score) {
      if (score === 1) return "green";
      else return "red";
    },
  },
};
</script>

<style scoped>
thead {
  background-color: #f3e5ff;
}
.v-data-table-header {
  background-color: #f3e5ff;
}
.docAction {
  margin-right: 2em;
  margin-top: 0.5em;
  padding-left: 12px;
}
</style>
