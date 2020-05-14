package fi.uta.cs.weto.actions.forum;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.*;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

public class ForumActions
{
  public static class ForumBean
  {
    private Integer id;
    private Integer rank;
    private String title;
    private String text;
    private String author;
    private Integer authorId;
    private String date;

    public ForumBean(Integer id, Integer rank, String title, String text,
            String author, Integer authorId, String date)
    {
      this.id = id;
      this.rank = rank;
      this.title = title;
      this.text = text;
      this.author = author;
      this.authorId = authorId;
      this.date = date;
    }

    public Integer getId()
    {
      return id;
    }

    public void setId(Integer id)
    {
      this.id = id;
    }

    public Integer getRank()
    {
      return rank;
    }

    public void setRank(Integer rank)
    {
      this.rank = rank;
    }

    public String getTitle()
    {
      return title;
    }

    public void setTitle(String title)
    {
      this.title = title;
    }

    public String getText()
    {
      return text;
    }

    public void setText(String text)
    {
      this.text = text;
    }

    public String getAuthor()
    {
      return author;
    }

    public void setAuthor(String author)
    {
      this.author = author;
    }

    public Integer getAuthorId()
    {
      return authorId;
    }

    public void setAuthorId(Integer authorId)
    {
      this.authorId = authorId;
    }

    public String getDate()
    {
      return date;
    }

    public void setDate(String date)
    {
      this.date = date;
    }

  }

  private static final String anonymousName = WetoUtilities.getMessageResource(
          "general.header.student");

  public static class View extends WetoCourseAction
  {
    private ArrayList<ForumBean> topicBeans = new ArrayList<>();
    private boolean canAddTopic;
    private boolean forumSubscribed;

    public View()
    {
      super(Tab.FORUM.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Task task = getTask();
      Integer userId = getCourseUserId();
      final String userIP = getNavigator().getUserIP();
      // Check rights for adding a forum topic
      WetoTimeStamp[] addPeriod = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.FORUM_TOPIC, getNavigator()
              .isTeacher());
      canAddTopic = (PermissionModel.checkTimeStampLimits(addPeriod)
              == PermissionModel.CURRENT);
      ArrayList<TagView> topicTags = TagView.selectByTaggedIdAndType(conn, task
              .getId(), TagType.FORUM_TOPIC.getValue());
      HashSet<Integer> teacherIdSet = new HashSet<>();
      for(UserTaskView teacher : UserTaskView.selectByTaskIdAndClusterType(conn,
              taskId, ClusterType.TEACHERS.getValue()))
      {
        teacherIdSet.add(teacher.getUserId());
      }
      HashMap<Integer, Integer> anonymousIdMap = new HashMap<>();
      int rank = 0;
      for(TagView tag : topicTags)
      {
        Integer authorId = tag.getAuthorId();
        String author = null;
        if(!(teacherIdSet.contains(authorId) || userId.equals(authorId)))
        {
          Integer anonymousId = anonymousIdMap.get(authorId);
          if(anonymousId == null)
          {
            anonymousId = anonymousIdMap.size() + 1;
            anonymousIdMap.put(authorId, anonymousId);
          }
          // Make students anonymous to each other
          author = anonymousName + " " + anonymousId.toString();
        }
        else
        {
          author = tag.getFirstName() + " " + tag.getLastName();
        }
        JsonObject topicJson = new JsonParser().parse(tag.getText())
                .getAsJsonObject();
        topicBeans.add(new ForumBean(tag.getId(), rank++, topicJson.get("title")
                .getAsString(), "", author, tag.getAuthorId(),
                new WetoTimeStamp(tag.getTimeStamp()).getDateString()));
      }
      // Check if forum is subscribed.
      try {
        Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn, taskId, -1, userId, TagType.FORUM_SUBSCRIPTION.getValue());
        forumSubscribed = true;
      }
      // Forum is not subscribed.
      catch (NoSuchItemException e) {
      }
      return SUCCESS;
    }

    public ArrayList<ForumBean> getTopicBeans()
    {
      return topicBeans;
    }

    public boolean isCanAddTopic()
    {
      return canAddTopic;
    }

    public boolean isForumSubscribed() {
      return forumSubscribed;
    }

    public void setForumSubscribed(boolean forumSubscribed) {
      this.forumSubscribed = forumSubscribed;
    }

  }

  public static class ViewTopic extends WetoCourseAction
  {
    private Integer topicId;
    private String topicTitle;
    private ArrayList<ForumBean> messageBeans = new ArrayList<>();
    private boolean canAddReply;
    private boolean subscribed;

    public ViewTopic()
    {
      super(Tab.FORUM.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      TagView topicTag = TagView.select1ByIdAndType(conn, topicId,
              TagType.FORUM_TOPIC.getValue());
      validateCourseSubtaskId(topicTag.getTaggedId());
      final String userIP = getNavigator().getUserIP();
      // Check rights for adding a forum topic
      WetoTimeStamp[] replyPeriod = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.FORUM_REPLY, getNavigator()
              .isTeacher());
      canAddReply = (PermissionModel.checkTimeStampLimits(replyPeriod)
              == PermissionModel.CURRENT);
      JsonObject messageJson = new JsonParser().parse(topicTag.getText())
              .getAsJsonObject();
      topicTitle = messageJson.get("title").getAsString();
      ArrayList<TagView> messageTags = new ArrayList<>();
      messageTags.add(topicTag);
      ArrayList<TagView> replyTags = TagView.selectByTaggedIdAndStatusAndType(
              conn, taskId, topicId, TagType.FORUM_MESSAGE.getValue());
      replyTags.sort(new Comparator<TagView>()
      {
        @Override
        public int compare(TagView a, TagView b)
        {
          return Integer.compare(a.getRank(), b.getRank());
        }

      });
      messageTags.addAll(replyTags);
      HashSet<Integer> teacherIdSet = new HashSet<>();
      for(UserTaskView teacher : UserTaskView.selectByTaskIdAndClusterType(conn,
              taskId, ClusterType.TEACHERS.getValue()))
      {
        teacherIdSet.add(teacher.getUserId());
      }
      HashMap<Integer, Integer> anonymousIdMap = new HashMap<>();
      int rank = 0;
      for(TagView tag : messageTags)
      {
        Integer authorId = tag.getAuthorId();
        String author = null;
        if(!(teacherIdSet.contains(authorId) || userId.equals(authorId)))
        {
          Integer anonymousId = anonymousIdMap.get(authorId);
          if(anonymousId == null)
          {
            anonymousId = anonymousIdMap.size() + 1;
            anonymousIdMap.put(authorId, anonymousId);
          }
          // Make students anonymous to each other
          author = anonymousName + " " + anonymousId.toString();
        }
        else
        {
          author = tag.getFirstName() + " " + tag.getLastName();
        }
        if(messageJson == null)
        {
          messageJson = new JsonParser().parse(tag.getText()).getAsJsonObject();
        }
        messageBeans.add(new ForumBean(tag.getId(), rank++, "", messageJson.get(
                "text").getAsString(), author, authorId, new WetoTimeStamp(tag
                        .getTimeStamp()).toString()));
        messageJson = null;
      }

      subscribed = isTopicSubscribed (conn, taskId, userId);
      return SUCCESS;
    }

    public boolean isTopicSubscribed (Connection connection, Integer taskId, Integer userId) {
      boolean topicSubscribed = false;
      // Check if user has subscribed topic.
      try {
        ArrayList<Tag> subscriptionTags = Tag.selectByTaggedIdAndAuthorIdAndType(connection, taskId, userId, TagType.FORUM_TOPIC_SUBSCRIPTION.getValue());
        for (int i = 0; i < subscriptionTags.size(); i++) {
          if (subscriptionTags.get(i).getStatus().equals(topicId)) {
            topicSubscribed = true;
            break;
          }
        }
      }
      catch (Exception ignored) {
      }
      return topicSubscribed;
    }

    public void setTopicId(Integer topicId)
    {
      this.topicId = topicId;
    }

    public Integer getTopicId()
    {
      return topicId;
    }

    public String getTopicTitle()
    {
      return topicTitle;
    }

    public ArrayList<ForumBean> getMessageBeans()
    {
      return messageBeans;
    }

    public boolean isCanAddReply()
    {
      return canAddReply;
    }

    public boolean isSubscribed() {
      return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
      this.subscribed = subscribed;
    }
  }

  public static class AddTopic extends WetoCourseAction
  {
    private String topicTitle;
    private String topicText;

    public AddTopic()
    {
      super(Tab.FORUM.getBit(), Tab.FORUM.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      final String userIP = getNavigator().getUserIP();
      // Check rights for adding a forum topic
      WetoTimeStamp[] addPeriod = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.FORUM_TOPIC, getNavigator()
              .isTeacher());
      if(PermissionModel.checkTimeStampLimits(addPeriod)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Insert the topic title and text into a JSON object.
      JsonObject topicJson = new JsonObject();
      topicJson.addProperty("title", topicTitle);
      topicJson.addProperty("text", topicText);
      Tag tag = new Tag();
      tag.setText(topicJson.toString());
      tag.setTaggedId(taskId);
      tag.setAuthorId(userId);
      tag.setType(TagType.FORUM_TOPIC.getValue());
      tag.insert(conn);
      addActionMessage(getText("forum.message.messageAdded"));
      createNotification(tag);
      return SUCCESS;
    }

    private void createNotification(Tag tag) {

      Connection masterConnection = getMasterConnection();
      Connection courseConnection = getCourseConnection();

      try {

        ArrayList<Tag> forumSubscriptions = Tag.selectByTaggedIdAndType(courseConnection, getTaskId(), TagType.FORUM_SUBSCRIPTION.getValue());

        // Get the teachers for the course
        HashSet<Integer> teacherIdSet = new HashSet<>();
        for (UserTaskView teacher : UserTaskView.selectByTaskIdAndClusterType(courseConnection, getTaskId(), ClusterType.TEACHERS.getValue())) {
          teacherIdSet.add(teacher.getUserId());
        }

        // Map values that will be put to the notification template
        UserAccount author = UserAccount.select1ById(courseConnection, getCourseUserId());
        HashMap<String, String> valueMap = new HashMap<>();
        // User name
        if (teacherIdSet.contains(author.getId())) {
          valueMap.put("&user;", author.getFirstName() + " " + author.getLastName());
        } else {
          valueMap.put("&user;", anonymousName);
        }

        // Forum topic
        JsonObject topicJson = new JsonParser().parse(tag.getText()).getAsJsonObject();
        valueMap.put("&forumTitle;", topicJson.get("title").toString());

        String notificationMessage = Notification.getMessageFromTemplate(Notification.FORUM_TOPIC, valueMap);
        String notificationLink = WetoUtilities.getAppBaseUrlFromServlet(getRequest())
                + "/viewForum.action?taskId=" + getTaskId()
                + "&tabId=" + getTabId()
                + "&dbId=" + getDbId();

        CourseImplementation masterCourse = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterConnection, getDbId(), getCourseTaskId());

        for (int i = 0; i < forumSubscriptions.size(); i++) {
          Integer authorId = forumSubscriptions.get(i).getAuthorId();
          if (!authorId.equals(getCourseUserId())) {
            UserAccount user = UserAccount.select1ById(courseConnection, authorId);
            UserAccount masterUser = UserAccount.select1ByLoginName(masterConnection, user.getLoginName());

            Notification notification = new Notification(masterUser.getId(), masterCourse.getMasterTaskId(), Notification.FORUM_POST, notificationLink);
            notification.setMessage(notificationMessage);
            notification.createNotification(masterConnection, courseConnection);
          }
        }


      }
      catch (Exception ignored) {
      }
    }

    public void setTopicTitle(String topicTitle)
    {
      this.topicTitle = (topicTitle != null) ? Jsoup.clean(topicTitle
              .replaceAll("\r\n", "\n"), "", Whitelist.simpleText(),
              new OutputSettings().prettyPrint(false)) : "";
    }

    public void setTopicText(String topicText)
    {
      this.topicText = (topicText != null) ? Jsoup.clean(topicText.replaceAll(
              "\r\n", "\n"), "", Whitelist.basicWithImages(),
              new OutputSettings().prettyPrint(false)) : "";
    }

  }

  public static class AddMessage extends WetoCourseAction
  {
    private Integer topicId;
    private String messageText;

    public AddMessage()
    {
      super(Tab.FORUM.getBit(), Tab.FORUM.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      try
      {
        Tag topicTag = Tag.select1ById(conn, topicId);
        if(!TagType.FORUM_TOPIC.getValue().equals(topicTag.getType()))
        {
          throw new NoSuchItemException();
        }
      }
      catch(NoSuchItemException e)
      {
        throw new WetoActionException(getText("forum.error.nonexistingTopic"));
      }
      final String userIP = getNavigator().getUserIP();
      // Check rights for adding a forum topic
      WetoTimeStamp[] replyPeriod = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.FORUM_REPLY, getNavigator()
              .isTeacher());
      if(PermissionModel.checkTimeStampLimits(replyPeriod)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Create message.
      JsonObject replyJson = new JsonObject();
      replyJson.addProperty("text", messageText);
      Tag tag = new Tag();
      tag.setText(replyJson.toString());
      tag.setTaggedId(taskId);
      tag.setStatus(topicId);
      tag.setAuthorId(userId);
      tag.setType(TagType.FORUM_MESSAGE.getValue());
      tag.setRank(TagView
              .selectByTaggedIdAndStatusAndType(conn, taskId, topicId,
                      TagType.FORUM_MESSAGE.getValue()).size());
      tag.insert(conn);
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.UPDATE_FORUM_MESSAGE
                .getValue(), tag.getId(), null, userIP).insert(conn);
      }
      addActionMessage(getText("forum.message.messageAdded"));
      createNotifications(messageText);
      return SUCCESS;
    }

    private void createNotifications(String messageText) {
      Connection masterConnection = getMasterConnection();
      Connection courseConnection = getCourseConnection();

      try {
        ArrayList<Tag> messages = new ArrayList<>();
        Tag topic = Tag.select1ById(courseConnection, topicId);
        messages.add(topic);
        messages.addAll(Tag.selectByTaggedIdAndStatusAndType(courseConnection, getTaskId(), topicId, TagType.FORUM_MESSAGE.getValue()));

        /*
        HashSet<Integer> messageAuthors = new HashSet<>();
        for(Tag message : messages) {
          if(!message.getAuthorId().equals(getCourseUserId())) {
            messageAuthors.add(message.getAuthorId());
          }
        }
        */

        ArrayList<Tag> topicSubscriptions = Tag.selectByTaggedIdAndStatusAndType(courseConnection, getTaskId(), topicId, TagType.FORUM_TOPIC_SUBSCRIPTION.getValue());

        // Get the teachers for the course
        HashSet<Integer> teacherIdSet = new HashSet<>();
        for(UserTaskView teacher : UserTaskView.selectByTaskIdAndClusterType(courseConnection,
                getTaskId(), ClusterType.TEACHERS.getValue()))
        {
          teacherIdSet.add(teacher.getUserId());
        }

        // Map values that will be put to the notification template
        UserAccount author = UserAccount.select1ById(courseConnection, getCourseUserId());
        HashMap<String, String> valueMap = new HashMap<>();
        // User name
        if(teacherIdSet.contains(author.getId())) {
          valueMap.put("&user;", author.getFirstName() + " " + author.getLastName());
        } else {
          valueMap.put("&user;", anonymousName);
        }
        // Forum topic
        JsonObject topicJson = new JsonParser().parse(topic.getText()).getAsJsonObject();
        valueMap.put("&forumTitle;", topicJson.get("title").toString());

        String notificationMessage = Notification.getMessageFromTemplate(Notification.FORUM_POST, valueMap);
        String notificationLink = WetoUtilities.getAppBaseUrlFromServlet(getRequest())
                + "/viewForumTopic.action?taskId=" + getTaskId()
                + "&tabId=" + getTabId()
                + "&dbId=" + getDbId()
                + "&topicId=" + getTopicId();

        CourseImplementation masterCourse = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterConnection, getDbId(), getCourseTaskId());
        /*
        // Send to all participants
        for(Integer authorId : messageAuthors) {
          UserAccount user = UserAccount.select1ById(courseConnection, authorId);
          UserAccount masterUser = UserAccount.select1ByLoginName(masterConnection, user.getLoginName());

          Notification notification = new Notification(masterUser.getId(), masterCourse.getMasterTaskId(), Notification.FORUM_POST, notificationLink);
          notification.setMessage(notificationMessage);
          notification.createNotification(masterConnection, courseConnection);
        }
        */
        for (int i = 0; i < topicSubscriptions.size(); i++ ) {
          Integer authorId = topicSubscriptions.get(i).getAuthorId();
          if (!authorId.equals(getCourseUserId())) {
            UserAccount user = UserAccount.select1ById(courseConnection, authorId);
            UserAccount masterUser = UserAccount.select1ByLoginName(masterConnection, user.getLoginName());

            Notification notification = new Notification(masterUser.getId(), masterCourse.getMasterTaskId(), Notification.FORUM_POST, notificationLink);
            notification.setMessage(notificationMessage);
            notification.createNotification(masterConnection, courseConnection);
          }
        }
      } catch (Exception ignored) {
      }
    }

    public Integer getTopicId()
    {
      return topicId;
    }

    public void setTopicId(Integer topicId)
    {
      this.topicId = topicId;
    }

    public void setMessageText(String messageText)
    {
      this.messageText = (messageText != null) ? Jsoup.clean(messageText
              .replaceAll("\r\n", "\n"), "", Whitelist.basicWithImages(),
              new OutputSettings().prettyPrint(false)) : "";
    }

  }

  public static class EditMessage extends WetoCourseAction
  {
    private Integer topicId;
    private Integer messageId;
    private String messageText;
    private boolean commitSave;
    private boolean commitDelete;

    public EditMessage()
    {
      super(Tab.FORUM.getBit(), Tab.FORUM.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      String result = INPUT;
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      Tag messageTag = Tag.select1ById(conn, messageId);
      // Check rights for editing the message
      if(!(getNavigator().isTeacher() || userId.equals(messageTag
              .getAuthorId())))
      {
        throw new WetoActionException(getText("ACCESS_DENIED"));
      }
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] replyPeriod = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.FORUM_REPLY, getNavigator()
              .isTeacher());
      if(PermissionModel.checkTimeStampLimits(replyPeriod)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      JsonObject messageJson = new JsonParser().parse(messageTag.getText())
              .getAsJsonObject();
      if(commitSave)
      {
        messageJson.addProperty("text", messageText);
        messageTag.setText(messageJson.toString());
        messageTag.update(conn);
        addActionMessage(getText("forum.message.messageSaved"));
        if(!getNavigator().isStudentRole())
        {
          new Log(getCourseTaskId(), taskId, userId,
                  LogEvent.UPDATE_FORUM_MESSAGE.getValue(), messageTag.getId(),
                  null, userIP).insert(conn);
        }
        result = SUCCESS;
      }
      else if(commitDelete)
      {
        if(!getNavigator().isTeacher())
        {
          throw new WetoActionException(getText("ACCESS_DENIED"));
        }
        addActionMessage(getText("forum.message.messageDeleted"));
        messageTag.delete(conn);
        result = SUCCESS;
      }
      else
      {
        messageText = messageJson.getAsJsonPrimitive("text").getAsString();
      }
      return result;
    }

    public Integer getTopicId()
    {
      return topicId;
    }

    public void setTopicId(Integer topicId)
    {
      this.topicId = topicId;
    }

    public Integer getMessageId()
    {
      return messageId;
    }

    public void setMessageId(Integer messageId)
    {
      this.messageId = messageId;
    }

    public String getMessageText()
    {
      return messageText;
    }

    public void setMessageText(String messageText)
    {
      this.messageText = (messageText != null) ? Jsoup.clean(messageText
              .replaceAll("\r\n", "\n"), "", Whitelist.basicWithImages(),
              new OutputSettings().prettyPrint(false)) : "";
    }

    public void setCommitSave(boolean commitSave)
    {
      this.commitSave = commitSave;
    }

    public void setCommitDelete(boolean commitDelete)
    {
      this.commitDelete = commitDelete;
    }

  }

  public static class SaveTopicSubscription extends WetoCourseAction {

    private Integer topicId;
    private boolean subscription;

    public SaveTopicSubscription() {
      super(Tab.FORUM.getBit(), 0, 0, 0);
    }

    public Integer getTopicId() {
      return topicId;
    }

    public void setTopicId(Integer topicId) {
      this.topicId = topicId;
    }

    public boolean isSubscription() {
      return subscription;
    }

    public void setSubscription(boolean subscription) {
      this.subscription = subscription;
    }

    @Override
    public String action() throws Exception {
      Connection courseConnection = getCourseConnection();
      int userId = getCourseUserId();
      int taskId = getTaskId();

      Tag tag = new Tag();
      if (subscription) {
        //tag.setText();
        tag.setTaggedId(taskId);
        tag.setStatus(topicId);
        tag.setAuthorId(userId);
        tag.setType(TagType.FORUM_TOPIC_SUBSCRIPTION.getValue());
        tag.setRank(-1);
        tag.insert(courseConnection);
        addActionMessage(getText("forum.message.topicSubscribed"));
      } else {
        ArrayList<Tag> subscriptionTags = Tag.selectByTaggedIdAndAuthorIdAndType(courseConnection, taskId, userId, TagType.FORUM_TOPIC_SUBSCRIPTION.getValue());
        for (int i = 0; i < subscriptionTags.size(); i++) {
          if (subscriptionTags.get(i).getStatus().equals(topicId)) {
            tag = subscriptionTags.get(i);
            break;
          }
        }
        tag.delete(courseConnection);
        addActionMessage(getText("forum.message.topicUnsubscribed"));
      }
      return SUCCESS;
    }
  }

  public static class SaveForumSubscription extends WetoCourseAction {

    boolean forumSubscription;

    public SaveForumSubscription() { super(Tab.FORUM.getBit(), 0, 0, 0); }

    public boolean isForumSubscription() {
      return forumSubscription;
    }

    public void setForumSubscription(boolean forumSubscription) {
      this.forumSubscription = forumSubscription;
    }

    public String action() throws Exception {
      Connection courseConnection = getCourseConnection();
      int userId = getCourseUserId();
      int taskId = getTaskId();

      Tag tag = new Tag();
      if(forumSubscription) {
        tag.setTaggedId(taskId);
        tag.setAuthorId(userId);
        tag.setType(TagType.FORUM_SUBSCRIPTION.getValue());
        tag.setRank(-1);
        tag.insert(courseConnection);
        addActionMessage(getText("forum.message.forumSubscribed"));
      }
      else {
        tag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(courseConnection, taskId, -1, userId, TagType.FORUM_SUBSCRIPTION.getValue());
        tag.delete(courseConnection);
        addActionMessage(getText("forum.message.forumUnsubscribed"));
      }
      return SUCCESS;
    }
  }
}
