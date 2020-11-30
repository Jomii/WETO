<template>
  <v-expansion-panels>
    <v-expansion-panel key="submission" >
      <v-expansion-panel-header>
        <h2>Submission</h2>
      </v-expansion-panel-header>
      <v-expansion-panel-content>

        <div class="submission">
          <h3 class="submissionPeriod">Submission period: {{submissionPeriod[0]}} - {{submissionPeriod[1]}}</h3>
          <file-submit
            v-if="submissionPeriodActive === true"
            v-bind:submission="submission"
            v-bind:file-patterns="filePatterns"
            v-on:add="createSubmission"
            v-on:refresh="getSubmissions"
            ref="fileSubmit"
          />
          <user-submission
            v-if="submission !== null"
            v-bind:submission-status="submissionStatus"
            v-bind:submission="submission"
            v-bind:documents="documents"
            v-bind:submission-period-active="submissionPeriodActive"
            v-on:deleteDocument="getSubmissions"
          />
          <v-row v-if="submission && submissionPeriodActive === true" class="submissionActions" align="center" justify="space-around">
            <v-btn color="primary" @click="completeSubmission" :disabled="!documents.length || submissionStatus !== 'Not submitted'">
              <v-icon>check</v-icon>
              Complete submission
            </v-btn>
            <v-dialog
              v-model="dialog"
              width="400"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn color="error" v-bind="attrs" v-on="on">
                  <v-icon>delete</v-icon>
                  Delete submission
                </v-btn>
              </template>
              <v-card>
                <v-card-title>
                  Delete submission
                </v-card-title>
                <v-card-text>
                  Are you sure you want to delete this submission?
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="secondary" text @click="dialog = false">Cancel</v-btn>
                  <v-btn color="primary" text @click="dialog = false; deleteSubmission(submission.id);">Delete</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-row>
        </div>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
  import api from '../backend-api'
  import router from '../router'
  import UserSubmission from '../components/UserSubmission'
  import FileSubmit from '../components/FileSubmit.vue'

  export default {
    name: 'submission',
    data() {
      return {
        backendResponse: [],
        errors: [],
        submission: null,
        submissionStatus: null,
        filePatterns: "*.*",
        documents: [],
        submissionPeriod: [],
        submissionPeriodActive: null,
        dialog: false,
        HTML: 0,
        MULTIPLE_CHOICE: 1,
        ESSAY: 2,
        SURVEY: 3,
        PROGRAM: 4
      }
    },
    computed: {
      taskId() {
        return this.$store.getters.currentTask;
      },
      dbId() {
        return this.$store.getters.selectedCourse.databaseId;
      },
      tabId() {
        return 4;
      },
      taskName() {
        return this.$store.getters.selectedCourse.name;
      },
      user() {
        return this.$store.getters.user;
      },
      subTasks() {
        return this.$store.getters.subTasks;
      }
    },
    created() {
      this.checkLogin();
      this.checkCourseSelection();
      this.fetchData();
      this.viewSubmissions();
      this.getSubmissions();
    },
    watch: {
      taskId() {
        this.fetchData();
      }
    },
    components: {
      UserSubmission,
      FileSubmit
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(error => {
          this.errors.push(error);
          window.location.replace("http://localhost:4545/");
        })
      },
      fetchData() {
        this.$store.commit("changeStatus", "loading");
        api.getCourseTask(this.dbId, this.taskId, this.tabId).then(response => {
          this.backendResponse = response.data;
          this.$store.commit("setSubTasks", response.data.subtasks);
          this.$store.commit("changeStatus", "normal");
        })
          .catch(error => {
            this.errors.push(error);
            this.$store.commit("changeStatus", "error");
          })
      },
      checkCourseSelection() {
        if (this.taskId === null || this.dbId === null) {
          this.clearSelectedCourse();
          router.push('/');
        }
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      },
      switchTask(id) {
        this.$store.commit("setTask", id);
      },
      async getSubmissions() {
        let promise = api.getSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
          this.filePatterns = response.data.patternDescriptions;
          if (response.data.submissions.length) {
            const s = response.data.submissions[0];
            this.submission = s;
            this.submissionStatus = response.data.submissionStates[s.status];
            api.getDocuments(this.submission.id, this.dbId, this.taskId, this.tabId).then(res => {
              this.documents = res.data.documents;
            }).catch((err) => {
              console.log(err);
            });
          } else {
            this.submission = null;
            this.submissionStatus = null;
            this.documents = [];
          }
        }).catch(err => {
          console.log(err);
          // this.errors.push(err);
        });

        return promise
      },
      viewSubmissions() {
        api.viewSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
          this.submissionPeriod = response.data.generalSubmissionPeriod;
          this.submissionPeriodActive = response.data.submissionPeriodActive;
        }).catch(err => {
          console.log(err);
          this.errors.push(err);
        })
      },
      createSubmission() {
        if (this.submission === null) {
          api.createSubmission(this.user.idData.value, this.dbId, this.taskId, this.tabId).then(() => {
            return this.getSubmissions().then(() => {
              this.$refs.fileSubmit.uploadFiles();
            })
          }).catch(err => {
            console.log(err);
          });
        }
      },
      deleteSubmission() {
        api.deleteSubmission(this.submission.id, true, this.dbId, this.taskId, this.tabId).then(() => {
            this.getSubmissions();
        }).catch(err => {
            console.log(err);
          this.errors.push(err);
        });
      },
      completeSubmission() {
        api.completeSubmission(this.submission.id, this.dbId, this.taskId, this.tabId).then(() => {
          this.getSubmissions();
          this.$refs.fileSubmit.removeAllFiles();
        }).catch(err => {
          console.log(err);
          this.errors.push(err);
        });
      }
    }
  }
</script>
<style scoped>
  h2 {
    color: #32005C;
  }
  .submissionPeriod {
    text-align: center;
    color: #32005C;
  }
  .submissionActions {
    margin-top: 1em;
  }
  .submissionActions > * {
    margin-top: 1em;
  }
  .submission {
    padding: 1em;
    border: 1px #102027 solid;
    border-radius: 0.5em;
  }
</style>