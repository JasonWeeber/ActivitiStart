import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

public class HelloWord {

	// 流程引擎
	private ProcessEngine processEngine;
	// 布署接口
	private RepositoryService repositoryService;
	// 任务接口
	private TaskService taskService;
	// 运行时接口
	private RuntimeService runtimeService;
	// 测试用户id
	private String leaderId = "abc123";
	// hr用户id
	private String hrId = "hr123";
	// 员工id
	private String employeeId = "em123";

	@Before
	public void initProcessEngine() {
		// 创建流程引擎,使用内存数据库
		processEngine = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(
						"activiti.cfg.xml").buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		taskService = processEngine.getTaskService();
		runtimeService = processEngine.getRuntimeService();
	}

	/**
	 * 布署流程
	 */
	@Test
	public void deployProcess() {
		// 通过文件布署流程
		repositoryService.createDeployment()
				.addClasspathResource("diagrams/ExplorerProcess.bpmn").deploy();
		// 验证已布署流程定义
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().singleResult();
		assert "process_pool1".equals(processDefinition.getKey());
	}

	/**
	 * 初始化一个流程实例
	 */
	@Test
	public void initProcess() {
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("process_pool1");
		// 判断实例是否启动
		assert processInstance != null;
		// 输出流程实例的id与流程定义的id
		System.out.println("流程实例的id:" + processInstance.getProcessInstanceId()
				+ ",流程定义的id:" + processInstance.getProcessDefinitionId());
		System.out.println("结束");
	}

	/**
	 * 用户认领任务
	 */
	@Test
	public void leaderClaimTask() {
		// 获取用户身份为leader的用户的权限
		List<Task> taskList = taskService.createTaskQuery()
				.taskCandidateGroup("leader").list();
		for (Task task : taskList) {
			System.out.println("当前未处理任务:" + task.getName());
			// 用户签收任务
			taskService.claim(task.getId(), leaderId);
		}
	}

	/**
	 * 查询用户认领的任务
	 */
	@Test
	public void leaderTaskQuery() {
		// 根据用户id,查询用户认领的任务
		List<Task> taskList = taskService.createTaskQuery()
				.taskAssignee(leaderId).list();
		for (Task task : taskList) {
			System.out.println("用户认领任务:" + task.getName());
		}
	}

	/**
	 * 查询用户认领的任务,并完成任务
	 */
	@Test
	public void leaderCompleteTask() {
		// 根据用户id,查询用户认领的任务
		List<Task> taskList = taskService.createTaskQuery()
				.taskAssignee(leaderId).list();
		for (Task task : taskList) {
			System.out.println("用户认领任务:" + task.getName());
			// 传入参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("leaderType", 1);
			// 完成任务
			taskService.complete(task.getId(), params);
		}
	}

	/**
	 * hr任务查询
	 */
	@Test
	public void hrTaskQuery() {
		// 根据hr权限查询任务
		List<Task> taskList = taskService.createTaskQuery()
				.taskCandidateGroup("hr").list();
		for (Task task : taskList) {
			System.out.println("当前任务:" + task.getName());
		}
	}

	/**
	 * hr认领所有任务
	 */
	@Test
	public void hrClaimTask() {
		// 根据hr权限查询任务
		List<Task> taskList = taskService.createTaskQuery()
				.taskCandidateGroup("hr").list();
		for (Task task : taskList) {
			System.out.println("当前任务:" + task.getName());
			taskService.claim(task.getId(), hrId);
		}
	}

	/**
	 * hr完成任务
	 */
	@Test
	public void hrCompleteTask() {
		// 根据hr id 查询当前用户待处理任务
		List<Task> taskList = taskService.createTaskQuery().taskAssignee(hrId)
				.list();
		for (Task task : taskList) {
			System.out.println("当前任务:" + task.getName());
			// 参数信息
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("hrType", 1);
			taskService.complete(task.getId(), params);
		}
	}

	/**
	 * 员工完成任务
	 */
	@Test
	public void employeeComplete() {
		List<Task> taskList = taskService.createTaskQuery()
				.taskCandidateGroup("employee").list();
		// 认领并完成任务
		for (Task task : taskList) {
			// 认领任务
			taskService.claim(task.getId(), employeeId);
			// 完成任务
			taskService.complete(task.getId());
		}
	}

}
