package cz.neumimto.rpg.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillTree;
import cz.neumito.rpg.rest.CharacterData;
import cz.neumito.rpg.rest.RestService;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TestGson {

	@Test
	public void test() {
		RestService restService = new RestService();
		SkillTree tree = new SkillTree();
		final ISkill mock = mock(ISkill.class);
		when(mock.getName()).thenReturn("skill1");
		when(mock.getIconURL()).thenReturn("url");
		tree.getSkills().put("skill1", new SkillData("skill1") {{
			setSkill(mock);
		}});

		ISkill mock1 = mock(ISkill.class);
		when(mock1.getName()).thenReturn("skill2");
		when(mock1.getIconURL()).thenReturn("url2");
		tree.getSkills().put("skill2", new SkillData("skill2") {{
			setSkill(mock1);
		}});

		ISkill mock2 = mock(ISkill.class);
		when(mock2.getName()).thenReturn("skill3");
		when(mock2.getIconURL()).thenReturn("url3");
		tree.getSkills().put("skill3", new SkillData("skill3") {{
			setSkill(mock2);
		}});

		SkillData skill3 = tree.getSkills().get("skill3");
		skill3.getSoftDepends().add(tree.getSkills().get("skill2"));
		skill3.getHardDepends().add(tree.getSkills().get("skill1"));
		System.out.println(restService.toJson(tree));

	}

	@Test
	public void getCharacterData() throws InterruptedException {
		CharacterData data = new CharacterData();
		data.setCharname("Nicitel");
		data.setLevel(89);
		data.setClassname("fda");
		System.out.println(new Gson().toJson(data));
	}


	@Test
	public void test2() {
		Map<String, ExtendedSkillInfo> map = new HashMap<>();

		ExtendedSkillInfo i = new ExtendedSkillInfo();
		i.setLevel(10);
		final ISkill mock = mock(ISkill.class);
		when(mock.getName()).thenReturn("skill1");
		when(mock.getIconURL()).thenReturn("url");
		i.setSkill(mock);

		map.put(i.getSkill().getName(), i);
		ExtendedSkillInfo q = new ExtendedSkillInfo();
		q.setLevel(10);
		final ISkill qmock = mock(ISkill.class);
		when(qmock.getName()).thenReturn("skill2");
		when(qmock.getIconURL()).thenReturn("url");
		q.setSkill(qmock);
		map.put(q.getSkill().getName(), q);

		System.out.println(new RestService().toJson(map));
	}


}
