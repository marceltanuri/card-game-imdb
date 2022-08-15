package com.tanuri.adaprova3;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanuri.adaprova3.model.Carta;
import com.tanuri.adaprova3.model.Rodada;

@SpringBootTest
@AutoConfigureMockMvc
public class AdaProva3_IntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void qualquer_endpoint_deve_exigir_autenticacao() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isUnauthorized());
		this.mockMvc.perform(post("/")).andDo(print()).andExpect(status().isUnauthorized());
		this.mockMvc.perform(get("/top10")).andDo(print()).andExpect(status().isUnauthorized());
	}

	@Test
	public void o_endpoint_iniciar_deve_responder_200_se_jogador_for_valido() throws Exception {
		this.mockMvc.perform(post("/iniciar").with(httpBasic("user1", "password"))).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void o_endpoint_iniciar_deve_responder_200_contendo_um_objeto_jogada() throws Exception {
		this.mockMvc.perform(post("/iniciar").with(httpBasic("user1", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();
	}

	@Test
	public void o_endpoint_status_deve_responder_200_contendo_um_objeto_partida_de_uma_partida_em_andamento()
			throws Exception {
		this.mockMvc.perform(post("/iniciar").with(httpBasic("user1", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();

		this.mockMvc.perform(get("/status").with(httpBasic("user1", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.emAndamento").value("true")).andReturn();
	}

	@Test
	public void o_endpoint_desistir_deve_responder_200_contendo_um_objeto_partida_com_estado_emAndamento_igual_a_false()
			throws Exception {
		this.mockMvc.perform(post("/iniciar").with(httpBasic("user2", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();

		this.mockMvc.perform(post("/desistir").with(httpBasic("user2", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.emAndamento").value("false")).andReturn();
	}

	@Test
	public void o_endpoint_status_deve_responder_200_contendo_um_objeto_partida_de_uma_partida_finalizada()
			throws Exception {

		this.mockMvc.perform(post("/iniciar").with(httpBasic("user3", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();

		this.mockMvc.perform(post("/desistir").with(httpBasic("user3", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.emAndamento").value("false")).andReturn();

		this.mockMvc.perform(get("/status").with(httpBasic("user3", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.emAndamento").value("false")).andReturn();
	}

	@Test
	public void o_endpoint_proxima_deve_responder_200_contendo_um_objeto_proxima_jogada() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/iniciar").with(httpBasic("user4", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();

		String rodadaJson = result.getResponse().getContentAsString();
		Rodada rodada = objectMapper.readValue(rodadaJson, Rodada.class);

		Carta[] cartas = rodada.getCartas().toArray(new Carta[rodada.getCartas().size()]);
		this.mockMvc.perform(post("/proxima/" + cartas[0].getImdbId()).with(httpBasic("user4", "password")))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("2"))
				.andReturn();

	}

	@Test
	public void o_endpoint_proxima_deve_responder_400_se_a_resposta_for_invalida() throws Exception {
		this.mockMvc.perform(post("/iniciar").with(httpBasic("user5", "password"))).andDo(print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroDaRodada").value("1")).andReturn();

		this.mockMvc.perform(post("/proxima/").with(httpBasic("user5", "password"))).andDo(print())
				.andExpect(status().isNotFound()).andReturn();

		this.mockMvc.perform(post("/proxima/xxxxxx").with(httpBasic("user5", "password"))).andDo(print())
				.andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void o_endpoint_top10_deve_responder_200_com_o_ranking_dos_10_jogadores_com_maior_score() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/top10").with(httpBasic("user", "password"))).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String rankingJson = result.getResponse().getContentAsString();
		List<?> jogadores = objectMapper.readValue(rankingJson, List.class);
		assertTrue(jogadores.size() == 10);

	}

	@Test
	public void o_endpoint_meuRanking_deve_responder_200_com_a_posicao_do_jogador_no_ranking() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/meuRanking").with(httpBasic("user6", "password"))).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String meuRankingJson = result.getResponse().getContentAsString();
		assertTrue(Long.valueOf(meuRankingJson) != null);

	}

}
