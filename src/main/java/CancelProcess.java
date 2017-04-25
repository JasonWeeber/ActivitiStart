import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

public class CancelProcess {

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
				.addClasspathResource("diagrams/CancelProcessInst.bpmn")
				.deploy();
		// 验证已布署流程定义
		List<ProcessDefinition> processDefinition = repositoryService
				.createProcessDefinitionQuery().list();
		assert "cancelProcess".equals(processDefinition.get(1).getKey());
		System.out.println("布署流程成功");
	}

	/**
	 * 运行一个流程实例
	 */
	@Test
	public void runInst() {
		// 运行一个流程实例
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("cancelProcess");
		assert processInstance != null;
		System.out.println("流程实例启动成功!");
	}

}
