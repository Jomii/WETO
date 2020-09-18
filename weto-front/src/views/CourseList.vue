<template>
  <div class="courseList">
    
    <div v-if="status === 'loading'" class="text-xs-center">
      <v-progress-circular indeterminate color="secondary" size="70" width="7"/>
    </div>
    
    <div v-if="status === 'error'" class="text-xs-center">
      <h4>Tapahtui virhe</h4>
    </div>
    
    <div v-if="status === 'normal'">
      <v-container v-for="subject in backendResponse.subjects" :key="subject.name" class="mt-6">
        <v-card>
          <h1 class="subjects pl-2">{{ subject.name }}</h1>
        </v-card>
        
        <v-card>
          <div clasS="activeCourses">
            <v-list v-if="backendResponse.registeredCourses[subject.id] !== null">
              <h2 class="pl-3">Your active courses</h2>
              <v-list-item v-for="course in backendResponse.registeredCourses[subject.id]" :key="course.name">
                <v-list-item-content class="pl-6">
                  <a @click="selectCourse(course)">
                    <h3>- {{ course.name }}</h3>
                  </a>
                  <span v-if="backendResponse.viewPeriods[course.masterTaskId][0] !== null ||
                              backendResponse.viewPeriods[course.masterTaskId][1] !== null">
                    ({{ backendResponse.viewPeriods[course.masterTaskId][0] }} -
                    {{ backendResponse.viewPeriods[course.masterTaskId][1] }})
                  </span>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </div>
        </v-card>
        
        <v-card>
          <div class="availableCourses">
            <v-list v-if="backendResponse.courses[subject.id] !== null" class="availableCourses">
              <h2 class="pl-3">Available courses</h2>
              <v-list-item v-for="course in backendResponse.courses[subject.id]" :key="course.name">
                <v-list-item-content>
                  <a @click="selectCourse(course)">
                    <h3>- {{ course.name }}</h3>
                  </a>
                  <span>
                    (Registration open until:
                    {{ backendResponse.registerPeriods[course.masterTaskId][1] }}
                  </span>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </div>
        </v-card>
        
        <v-card>
          <div v-if="backendResponse.inactiveCourses !== null" class="inactiveCourses">
            <v-expansion-panels>
              <v-expansion-panel>
                <v-expansion-panel-header class="my-2">
                  <h2>Inactive courses</h2>
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  <v-list>
                    <v-list-item v-for="course in backendResponse.inactiveCourses" :key="course.name">
                      <v-list-item-content class="pl-6">
                        <a @click="selectCourse(course)">
                          <h3>- {{ course.name }}</h3>
                        </a>
                        <span v-if="backendResponse.viewPeriods[course.masterTaskId][0] !== null ||
                                    backendResponse.viewPeriods[course.masterTaskId][1] !== null">
                        ({{ backendResponse.viewPeriods[course.masterTaskId][0] }} -
                        {{ backendResponse.viewPeriods[course.masterTaskId][1] }})
                        </span>
                      </v-list-item-content>
                    </v-list-item>
                  </v-list>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
          </div>
        </v-card>
        <p>hello world -Paavo</p>
      </v-container>
    </div>
  </div>
</template>

<script>
  import api from '../backend-api'
  import router from '../router'

  export default {
    name: 'course-list',
    data() {
      return {
        backendResponse: [],
        errors: [],
        user: this.$store.getters.user
      }
    },
    computed: {
      status() {
        return this.$store.getters.status;
      }
    },
    created() {
      this.checkLogin();
      this.getUser();
      this.clearSelectedCourse();
      this.fetchData();
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(error => {
          this.errors.push(error);
          window.location.replace("http://localhost:8080/weto5/listCourses.action");
        })
      },
      fetchData() {
        this.$store.commit("changeStatus", "loading");
        api.getCourses().then(response => {
          this.backendResponse = response.data;
          this.$store.commit("changeStatus", "normal");
        })
          .catch(error => {
            this.errors.push(error);
            this.$store.commit("changeStatus", "error");
          })
      },
      selectCourse(course) {
        this.$store.commit("selectCourse", course);
        this.$store.commit("setTask", course.courseTaskId);
        this.$store.commit("createSubTaskTree", course);
        router.push('/task')
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      },
      getUser() {
        api.getUser().then(response => {
          this.$store.commit("logUser", JSON.parse(response.data));
        })
      }
    }
  }
</script>

<style>
  .subjects {
    border-left: 4px solid #4e008e;
  }
  
  .activeCourses {
    border-left: 4px solid #c3b9d7;
  }
  
  .availableCourses {
    border-left: 4px solid #c3b9d7;
  }
  
  .inactiveCourses {
    border-left: 4px solid #3a4b54;
  }
</style>