package com.giffing.wicket.spring.boot.example.web.pages.home;

import com.giffing.wicket.spring.boot.example.entity.Customer;
import com.giffing.wicket.spring.boot.example.entity.Roles;
import com.giffing.wicket.spring.boot.example.entity.Status;
import com.giffing.wicket.spring.boot.example.entity.Tasks;
import com.giffing.wicket.spring.boot.example.web.pages.login.LoginPage;
import com.giffing.wicket.spring.boot.example.web.service.CustomerService;
import com.giffing.wicket.spring.boot.example.web.service.TaskService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.extensions.ajax.markup.html.repeater.AjaxListPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wicketstuff.annotation.mount.MountPath;
import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.giffing.wicket.spring.boot.example.web.pages.BasePage;


import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WicketHomePage
@MountPath("home")
public class HomePage extends BasePage {

	@SpringBean
	private CustomerService customerService;
	@SpringBean
	private TaskService taskService;
	final Form<Void> form;
	private DataView<Tasks> taskDataView;
	private ListDataProvider<Tasks> dataProvider;
	private String selectedCustomer;
	private Status selectedTask;
	private ModalDialog modalDialog;
	private ModalDialog updateDialog;
	private ModalDialog addDialog;
	private ModalDialog logoutDialog;
	private DropDownChoice<String> dropUsers;
	private DropDownChoice<Status> dropStatus;
	private static String flag = "zero";
	public String getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(String selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public Status getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(Status selectedTask) {
		this.selectedTask = selectedTask;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class, "newStyle.css")));
	}
	public HomePage(){

		List<String> customerNames = customerService.getAllUsernames();
		List<Status> taskStatus = taskService.getAllStatuses();
		List<Status> allStatus = Arrays.asList(Status.values());
		form = new Form<Void>("form");
		form.setOutputMarkupId(true);
		add(form);

		// drop down testing
		dropUsers =
				new DropDownChoice<>("customer_opt",new PropertyModel<>(this,"selectedCustomer"),customerNames);
		dropUsers.setNullValid(true);
		dropStatus =
				new DropDownChoice<>("tasks_opt",new PropertyModel<>(this,"selectedTask"),allStatus);
		dropStatus.setNullValid(true);
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		form.add(dropUsers);
		form.add(dropStatus);
		form.add(feedbackPanel);


		List<Tasks> tasks = taskService.getAllTasks();
		dataProvider = new ListDataProvider<>(tasks);
		taskDataView = createTaskDataView("simple", dataProvider);
		taskDataView.setOutputMarkupId(true);
		add(taskDataView);


		form.add(new AjaxButton("filter"){
			@Override
			protected void onSubmit(AjaxRequestTarget target){

				String selectedCustomer = dropUsers.getModelObject();
				Status selectedTask = dropStatus.getModelObject();


				System.out.println("Selected customer: " + dropUsers.getModelObject());
				System.out.println(dropStatus.getModelObject());

				if(selectedCustomer!=null && selectedTask==null){
					updateTable(taskService.getTasksByCustomerUserName(selectedCustomer),target);
					flag = "one";
				} else if(selectedTask!=null && selectedCustomer==null){
					updateTable(taskService.getTasksByStatus(selectedTask),target);
					flag = "two";
				} else if(selectedCustomer!=null && selectedTask!=null){
					//info("You can't select two options");
					//target.add(feedbackPanel);
					updateTable(taskService.getTasksFromUserNameAndStatus(selectedCustomer,selectedTask),target);
					flag = "three";
				} else if(selectedTask==null && selectedCustomer==null) {
					updateTable(taskService.getAllTasks(),target);
				}
				dropStatus.setChoices(allStatus);
				target.add(form);
			}

		});
		// remove modal dialog logic
		modalDialog = new ModalDialog("modalDialog");
		modalDialog.add(new DefaultTheme());
		modalDialog.trapFocus();
		modalDialog.closeOnEscape();
		add(modalDialog);

		// update modal dialog logic
		updateDialog = new ModalDialog("updateDialog");
		updateDialog.add(new DefaultTheme());
		updateDialog.trapFocus();
		updateDialog.closeOnEscape();
		add(updateDialog);

		//add new task dialog logic
		addDialog = new ModalDialog("addTaskDialog");
		addDialog.add(new DefaultTheme());
		addDialog.trapFocus();
		addDialog.closeOnEscape();
		add(addDialog);

		AjaxButton addNewTask = new AjaxButton("addTask",form) {
			@Override
			public void onSubmit(AjaxRequestTarget target){
				System.out.println("Adding new task");
				addDialog.setContent(new AddTaskFragment(ModalDialog.CONTENT_ID));
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
				item.add(new Label("creationDate", task.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString()));
				item.add(new Label("updateDate", task.getUpdateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString()));
				item.add(new Label("status", task.getStatus().name()));
				item.add(new Label("description", task.getDescription()));
				item.add(AttributeModifier.replace("class", () -> (item.getIndex() % 2 == 1) ? "even" : "odd"));


				AjaxButton removeLink = new AjaxButton("remove",form) {
					@Override
					public void onSubmit(AjaxRequestTarget target) {
						modalDialog.setContent(new RemoveTaskFragment(ModalDialog.CONTENT_ID,item.getModel()));
							target.add(modalDialog);
							modalDialog.open(target);
					}
				};

				AjaxButton edithLink = new AjaxButton("edith",form) {
					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						System.out.println("selected in edith button");
						updateDialog.setContent(new UpdateModal(ModalDialog.CONTENT_ID,item.getModel()));
						target.add(updateDialog);
						updateDialog.open(target);
					}
				};
				edithLink.setDefaultFormProcessing(false);
				removeLink.setDefaultFormProcessing(false);
				item.add(removeLink);
				item.add(edithLink);

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

	public List<Tasks> returnCaseMethod(String caseName) {
		List<Tasks> tasks = taskService.getAllTasks();
		switch (caseName) {
			case "one" -> tasks = taskService.getTasksByCustomerUserName(dropUsers.getModelObject());
			case "two" -> tasks = taskService.getTasksByStatus(dropStatus.getModelObject());
			case "three" -> tasks = taskService.getTasksFromUserNameAndStatus(dropUsers.getModelObject(),dropStatus.getModelObject());
		}
		return tasks;
	}

	// modal dialog remove class
	private class RemoveTaskFragment extends Fragment {
		private IModel<Tasks> taskModel;

		public RemoveTaskFragment(String id, IModel<Tasks> taskModel ) {
			super(id, "removeTaskFragment", HomePage.this);
			this.taskModel = taskModel;
			Form<Void> form1 = new Form<>("modal");
			form1.setOutputMarkupId(true);
			add(form1);
			form1.add(new Label("confirmMessage", "Are you sure you want to delete this task?"));

			// Confirm button
			AjaxButton confirmButton = new AjaxButton("confirm") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					taskService.deleteTask(taskModel.getObject());
					List<Tasks> tasks = taskService.getAllTasks();
					updateTable(tasks,target);
					modalDialog.close(target);
				}
			};

			add(confirmButton);
			form1.add(confirmButton);

			// Cancel button
			AjaxButton cancelButton = new AjaxButton("cancel") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					modalDialog.close(target);
				}
			};
			add(cancelButton);
			form1.add(cancelButton);
		}
	}
	// modal dialog update class
	public class UpdateModal extends Fragment {
		private final IModel<Tasks> taskModel;
		private final IModel<String> updateName;
		private final IModel<String> updateDescription;
		private final IModel<String> selectedUser;
		private final IModel<Status> updateStatus;
		public UpdateModal(String id, IModel<Tasks> taskModel) {
			super(id,"editTaskFragment",HomePage.this);
			this.taskModel = taskModel;
			this.updateName = new PropertyModel<>(taskModel, "name");
			this.updateDescription = new PropertyModel<>(taskModel, "description");
			this.selectedUser = new LoadableDetachableModel<String>() {
				@Override
				protected String load() {
					return taskModel.getObject().getCustomer().getUsername();
				}
			};
			this.updateStatus = new PropertyModel<>(taskModel, "status");
			Form<Void> form2 = new Form<>("modalUpdate");
			form2.setOutputMarkupId(true);
			add(form2);
			form2.add(new TextField<String>("updateTaskName",updateName));
			form2.add(new TextArea<String>("updateDescription",updateDescription));


			List<String> allUsers = customerService.getAllUsernames();
			List<Status> allStatus = Arrays.stream(Status.values()).toList();
			DropDownChoice<String> userDropDown = new DropDownChoice<>("users_list", selectedUser, allUsers);
			userDropDown.setNullValid(true);
			form2.add(userDropDown);
			DropDownChoice<Status> statusDropDown = new DropDownChoice<>("task_status", updateStatus,allStatus);
			form2.add(statusDropDown);
			AjaxButton updateButton = new AjaxButton("save") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					String updateTaskName = updateName.getObject();

					Tasks task = taskModel.getObject();
					task.setName(updateTaskName);
					task.setDescription(updateDescription.getObject());
					task.setStatus(updateStatus.getObject());
					taskService.updateTask(task);
					updateTable(returnCaseMethod(flag),target);
					updateDialog.close(target);
				}

			};
			add(updateButton);
			form2.add(updateButton);

			// Cancel button
			AjaxButton cancelButton = new AjaxButton("cancel") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					updateDialog.close(target);
				}
			};
			add(cancelButton);
			form2.add(cancelButton);
		}

	}

	// modal dialog addTask class
	private class AddTaskFragment extends Fragment {

		private final IModel<String> addName;
		private final IModel<String> addDescription;
		private final IModel<String> addCustomer;
		public AddTaskFragment(String id) {
			super(id, "addTaskFragment", HomePage.this);
			this.addName = Model.of("");
			this.addDescription = Model.of("");
			this.addCustomer = Model.of("");
			Form<Void> form = new Form<>("modalAddTask");
			form.setOutputMarkupId(true);
			add(form);
			List<String> allUsers = customerService.getAllUsernames();
			form.add(new TextField<String>("addTaskName",addName));
			form.add(new TextArea<String>("addDescription",addDescription));
			form.add(new DropDownChoice<String>("users_list",addCustomer,allUsers));
			// save task
			AjaxButton updateButton = new AjaxButton("save") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					taskService.saveTask(addName.getObject(),addCustomer.getObject(),addDescription.getObject());
					updateTable(returnCaseMethod(flag),target);
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
			super(id, "fragment", HomePage.this);

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
