package com.javacodingskill.spring.batch.example1.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.javacodingskill.spring.batch.example1.dto.EmployeeDTO;
import com.javacodingskill.spring.batch.example1.handler.SkipRecordCallback;
import com.javacodingskill.spring.batch.example1.listener.Demo7Listener;
import com.javacodingskill.spring.batch.example1.listener.ProcessListener;
import com.javacodingskill.spring.batch.example1.listener.ReaderListener;
import com.javacodingskill.spring.batch.example1.listener.WriterListener;
import com.javacodingskill.spring.batch.example1.mapper.EmployeeFileRowMapper;
import com.javacodingskill.spring.batch.example1.model.Employee;
import com.javacodingskill.spring.batch.example1.processor.EmployeeProcessor;

import net.bytebuddy.asm.Advice.Return;

@Configuration
public class Demo1 {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private EmployeeProcessor employeeProcessor;
	@Autowired
	private EmployeeFileRowMapper employeeFileRowMapper;
	@Autowired
	private DataSource dataSource;

	// @Qualifier(value = "demo1")
	@Bean
	public Job demo1Job() throws Exception {
		return this.jobBuilderFactory.get("demo1").start(step1Demo()).listener(demo7Listener()).build();
	}

	public Step step1Demo() throws Exception {
		return this.stepBuilderFactory.get("step1").<EmployeeDTO, Employee>chunk(5).reader(employeeReader())
				.processor(employeeProcessor).writer(employeeDOWriterDefault())
				.listener(readerListern())
				.listener(processListener())
				.listener(writerListener())
				.faultTolerant().skipPolicy(skipPolicy()).build();

	}

	@Bean
	@StepScope
	Resource inputFileResource(@Value("#{jobParameters[fileName]}") final String fileName) throws Exception {
		return new ClassPathResource(fileName);
	}

	@Bean
	@StepScope
	public FlatFileItemReader<EmployeeDTO> employeeReader() throws Exception {
		FlatFileItemReader<EmployeeDTO> reader = new FlatFileItemReader<>();
		reader.setResource(inputFileResource(null));
		reader.setLinesToSkip(1);
		reader.setSkippedLinesCallback(new SkipRecordCallback());
		
		reader.setLineMapper(new DefaultLineMapper<EmployeeDTO>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("employeeId", "firstName", "lastName", "email", "age");
						setDelimiter(",");
					}
				});
				setFieldSetMapper(employeeFileRowMapper);
			}
		});

		return reader;
	}

	@Bean
	public JdbcBatchItemWriter<Employee> employeeDOWriterDefault() {
		JdbcBatchItemWriter<Employee> itemWriter = new JdbcBatchItemWriter<Employee>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setSql(
				"insert into employee (employee_id, first_name, last_name, email, age) values (:employeeId, :firstName, :lastName, :email, :age)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		return itemWriter;
	}

	@Bean
	public ExecutionContext executionContext() {
		return new ExecutionContext();
	}

	@Bean
	public JobSkipPolicy skipPolicy() {
		return new JobSkipPolicy();
	}

	@Bean
	public Demo7Listener demo7Listener() {
		return new Demo7Listener();
	}

	@Bean
	public ReaderListener readerListern() {
		return new ReaderListener();
	}

	@Bean
	public ProcessListener processListener() {
		return new ProcessListener();
	}

	@Bean
	public WriterListener writerListener() {
		return new WriterListener();
	}
}
