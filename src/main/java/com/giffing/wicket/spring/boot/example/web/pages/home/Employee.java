package com.giffing.wicket.spring.boot.example.web.pages.home;

import com.giffing.wicket.spring.boot.example.entity.Status;
import com.giffing.wicket.spring.boot.example.entity.Tasks;
import com.giffing.wicket.spring.boot.example.web.pages.BasePage;
import com.giffing.wicket.spring.boot.example.web.pages.login.LoginPage;
import com.giffing.wicket.spring.boot.example.web.service.CustomerService;
import com.giffing.wicket.spring.boot.example.web.service.TaskService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@MountPath("/employee")
public class Employee  extends BasePage {

    @SpringBean
    private CustomerService customerService;
    @SpringBean
    private TaskService taskService;

    final Form<Void> form;
    private  String creationDate;
    private Status statusTask;
    private DataView<Tasks> taskDataView;
    private ListDataProvider<Tasks> dataProvider;
    private ModalDialog updateModalDialog;
    private  List<Status> taskStatus;
    private DropDownChoice<Status>  dropdownStatus;
    private DropDownChoice<String> dropdownDate;
    private ModalDialog addDialog;
    private ModalDialog logoutDialog;
    public Status getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(Status statusTask) {
        this.statusTask = statusTask;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(Employee.class, "newStyle.css")));
    }

    public Employee() {
        List<String> creationDates = taskService.getCreationDate();

        taskStatus = taskService.getStatusThisUser();
        form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);
        dropdownDate =
                new DropDownChoice<>("task_date",new PropertyModel<String>(this, "creationDate"),creationDates);
        dropdownStatus =
                new DropDownChoice<>("task_status",new PropertyModel<Status>(this, "statusTask"),taskStatus);
        dropdownStatus.setNullValid(true);
        dropdownDate.setNullValid(true);
        FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);

        form.add(dropdownDate);
        form.add(dropdownStatus);
        form.add(feedbackPanel);

        List<Tasks> allTaskFromLoginUser = taskService.getTaskFromLoginUser();
        dataProvider = new ListDataProvider<>(allTaskFromLoginUser);
        taskDataView = createTaskDataView("simple", dataProvider);
        taskDataView.setOutputMarkupId(true);
        add(taskDataView);

        form.add(new AjaxButton("filter") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String selectedCustomer = dropdownDate.getModelObject();
                Status selectedTask = dropdownStatus.getModelObject();
                System.out.println(selectedCustomer);
                if(selectedCustomer == null && selectedTask != null) {
                    updateTable(taskService.getAllTasksStatusFromLoginUser(selectedTask),target);
                } else if(selectedTask == null && selectedCustomer != null){
                    updateTable(taskService.getAllTasksCreationDateFromLoginUser(selectedCustomer),target);
                } else if(selectedCustomer == null && selectedTask == null){
                    updateTable(taskService.getTaskFromLoginUser(),target);
                } else if(selectedCustomer!=null && selectedTask!=null){
                    info("You can't select two options");
                    target.add(feedbackPanel);
                }
                dropdownStatus.setChoices(taskStatus);
                target.add(form);
            }
        });

        // modal dialog update logic
        updateModalDialog = new ModalDialog("updateDialog");
        updateModalDialog.add(new DefaultTheme());
        updateModalDialog.trapFocus();
        updateModalDialog.closeOnEscape();
        add(updateModalDialog);
        //modal dialog from add task logic
        addDialog = new ModalDialog("addTaskDialog");
        addDialog.add(new DefaultTheme());
        addDialog.trapFocus();
        addDialog.closeOnEscape();
        add(addDialog);

        AjaxButton addNewTask = new AjaxButton("addNewTask",form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                addDialog.setContent(new AddTaskModel(ModalDialog.CONTENT_ID));
                target.add(addDialog);
                addDialog.open(target);
            }
        };
        form.add(addNewTask);
        logoutDialog = new ModalDialog("modalLoginLink");
        logoutDialog.add(new DefaultTheme());
        logoutDialog.trapFocus();
        logoutDialog.closeOnEscape();
        add(logoutDialog);
        AjaxLink<Void> logoutLink = new AjaxLink<Void>("logoutLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                logoutDialog.setContent(new LogoutFragment(ModalDialog.CONTENT_ID));
                ajaxRequestTarget.add(logoutDialog);
                logoutDialog.open(ajaxRequestTarget);

            }
        };
        logoutLink.setOutputMarkupId(true);
        add(logoutLink);
    }

    private DataView<Tasks> createTaskDataView(String id, ListDataProvider<Tasks> dataProvider) {
        DataView<Tasks> dataView = new DataView<Tasks>(id, dataProvider){
            @Override
            protected void populateItem(Item<Tasks> item) {
                Tasks task = item.getModelObject();
                item.add(new Label("name", task.getName()));
                item.add(new Label("creationDate", task.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).toString()));
                item.add(new Label("updateDate", task.getUpdateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).toString()));
                item.add(new Label("status", task.getStatus().name()));
                item.add(new Label("description", task.getDescription()));

                AjaxButton updateButton = new AjaxButton("edit",form) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        updateModalDialog.setContent(new UpdateStatusModal(ModalDialog.CONTENT_ID,item.getModel()));
                        target.add(updateModalDialog);
                        updateModalDialog.open(target);
                    }
                };
                updateButton.setDefaultFormProcessing(false);
                item.add(updateButton);
            }
        };
        dataView.setOutputMarkupId(true);
        return dataView;
    }

    public void updateTable(List<Tasks> tasks, AjaxRequestTarget target) {
        ListDataProvider<Tasks> dataProvider = new ListDataProvider<>(tasks);
        DataView<Tasks> newTaskDataView = createTaskDataView("simple", dataProvider);
        newTaskDataView.setOutputMarkupId(true);
        taskDataView.getParent().replace(newTaskDataView);
        taskDataView = newTaskDataView;
        target.add(taskDataView.getParent());
    }

    public class UpdateStatusModal extends Fragment {

        private final IModel<Tasks> taskModel;
        private final IModel<Status> updateStatus;
        public UpdateStatusModal(String id, IModel<Tasks> taskModel) {
            super(id,"editTaskFragment",Employee.this);
            this.taskModel = taskModel;
            this.updateStatus = new PropertyModel<>(taskModel, "status");
            Form<Void> form = new Form<>("modalUpdate");
            form.setOutputMarkupId(true);
            add(form);

            List<Status> allStatus = new ArrayList<>(Arrays.asList(Status.values()));
            allStatus.remove(Status.RE_OPEN);
            allStatus.remove(Status.DONE);
            DropDownChoice<Status> dropdownStatusUpdate =
                    new DropDownChoice<>("task_status",updateStatus,allStatus);
            form.add(dropdownStatusUpdate);

            // update button
            AjaxButton update = new AjaxButton("save") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    Tasks task = taskModel.getObject();
                    task.setStatus(updateStatus.getObject());
                    Status thisTimeStatus = task.getStatus();
                    if(!(taskStatus.contains(updateStatus.getObject()))){

                        taskStatus.add(updateStatus.getObject());
                    }
                    dropdownStatus.setChoices(taskStatus);
                    taskService.updateTaskStatus(task);
                    updateTable(taskService.getTaskFromLoginUser(),target);
                    updateModalDialog.close(target);
                }
            };
            add(update);
            form.add(update);

            // Cancel button
            AjaxButton cancelButton = new AjaxButton("cancel") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    updateModalDialog.close(target);
                }
            };
            add(cancelButton);
            form.add(cancelButton);

        }
    }


    public class AddTaskModel extends Fragment {
        private final IModel<String> addName;
        private final IModel<String> addDescription;
        public AddTaskModel(String id) {
            super(id,"addTaskFragment",Employee.this);
            this.addName =  Model.of("");
            this.addDescription =  Model.of("");

            Form<Void> form = new Form<>("modalAddTask");
            form.setOutputMarkupId(true);
            add(form);

            form.add(new TextField<String>("addTaskName",addName));
            form.add(new TextArea<>("addDescription",addDescription));
            // save task
            AjaxButton updateButton = new AjaxButton("save") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    taskService.saveTaskFromLoginUser(addName.getObject(),addDescription.getObject());
                    updateTable(taskService.getTaskFromLoginUser(),target);
                    addDialog.close(target);
                }

            };
            add(updateButton);
            form.add(updateButton);

            // Cancel button
            AjaxButton cancelButton = new AjaxButton("cancel") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    addDialog.close(target);
                }
            };
            add(cancelButton);
            form.add(cancelButton);
        }
    }

    // modal dialog logout class
    private class LogoutFragment extends Fragment {

        public LogoutFragment(String id ) {
            super(id, "fragment", Employee.this);

            Form<Void> form = new Form<>("form");
            form.setOutputMarkupId(true);
            add(form);
            form.add(new Label("message", "Are you sure you want to logout?"));

            // Confirm button
            AjaxButton confirmButton = new AjaxButton("confirm") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    AuthenticatedWebSession session = AuthenticatedWebSession.get();
                    session.invalidate();
                    session.signOut();
                    SecurityContextHolder.clearContext();
                    setResponsePage(LoginPage.class);
                    logoutDialog.close(target);
                }
            };

            add(confirmButton);
            form.add(confirmButton);

            // Cancel button
            AjaxButton cancelButton = new AjaxButton("cancel") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    logoutDialog.close(target);
                }
            };
            add(cancelButton);
            form.add(cancelButton);
        }
    }
}
