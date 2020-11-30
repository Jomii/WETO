<template>
  <div class="grading">
    <h1>This is a grading page</h1>
    <TaskGrade />
  </div>
</template>

<script>
  import api from '../backend-api'
  import TaskGrade from '../components/TaskGrade'

  export default {
    name: 'grading',
    computed: {
      taskId() {
        return this.$store.getters.currentTask;
      },
      dbId() {
        return this.$store.getters.selectedCourse.databaseId
      },
      tabId() {
        return this.$store.getters.selectedCourse.tabId;
      }
    },
    created() {
      this.checkLogin();
      this.checkCourseSelection();
    },
    components:{
      TaskGrade
    },
    watch: {
      taskId() {
        this.fetchData();
      }
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(() => {
          this.$router.replace("/")
        })
      },
      checkCourseSelection() {
        if (this.taskId === null || this.dbId === null) {
          this.clearSelectedCourse();
          this.$router.replace("/")
        }
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      },
      /*fetchData() {
        api.getJSONNodeGrades(this.dbId, this.taskId, this.tabId).then(res => {
          console.log("NodeGrades: " + JSON.stringify(res));
        })

        api.getJSONStudentLeafGrades(this.dbId, this.taskId, this.tabId).then(res => {
          console.log("LeafGrades: " + JSON.stringify(res));
        })
      }*/
    }
  }
</script>