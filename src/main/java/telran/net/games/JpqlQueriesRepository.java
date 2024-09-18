package telran.net.games;
import java.time.*;
import java.util.List;

import jakarta.persistence.*;

public class JpqlQueriesRepository {
	private EntityManager em;

	public JpqlQueriesRepository(EntityManager em) {
		this.em = em;
	}
	
	public List<Game> getGamesFinished(boolean isFinished){
		TypedQuery<Game> query = em.createQuery(
				"select game from Game game where is_finished=?1",  // select ... - это проекция, результат select (то, что хотим получить)
				// ?1 - означает соответствующую позицию первую в query.setParameter(1, ...
				Game.class);
		List<Game> res = query.setParameter(1, isFinished).getResultList();
		return res;
	}
	
	public List<DateTimeSequence> getDateTimeSequence(LocalTime time) {
		TypedQuery<DateTimeSequence> query =
				em.createQuery("select date, sequence "
						+ "from Game where cast(date as time) < :time",
						DateTimeSequence.class);
		List<DateTimeSequence> res = query.setParameter("time", time).getResultList();
		return res;
	}
	
	public List<Integer> getBullsInMovesGamersBornAfter(LocalDate afterDate){
		TypedQuery<Integer> query = em.createQuery(
//				"select bulls from Move where gameGamer.gamer.birthdate > :afterdate", 
				"select bulls from Move where gameGamer.gamer.birthdate > ?1", 
				Integer.class);
		List<Integer> res = query.setParameter(1, afterDate).getResultList();
		return res;
	}
	
	public List<MinMaxAmount> getDistributionGamesMoves(int interval){
//select floor(game_moves/5) * 5 as min_moves, 
//floor(game_moves/5) * 5 +4 as max_moves,
//count(*) as amount 
//from
//(select count(*) as game_moves from game_gamer 
//join move on game_gamer.id=game_gamer_id
//group by game_id) group by min_moves order by min_moves		
		TypedQuery<MinMaxAmount> query = em.createQuery(
				"select floor(game_moves / :interval) * :interval as min_moves, "
				+ "floor(game_moves / :interval) * :interval + (:interval - 1) as max_moves, "
				+ "count(*) as amount "
				+ "from "
				+ "(select count(*) as game_moves from Move "
				+ "group by gameGamer.game.id) "
				+ "group by min_moves, max_moves order by min_moves", 
				MinMaxAmount.class);
		List<MinMaxAmount> res = query.setParameter("interval", interval).getResultList();
		return res;		
	}
	
	public List<Game> getGamersGreaterAge(double age){
//select * from game where id in (select game_id 
//from game_gamer
//join gamer on gamer_id=username
//group by game_id
//having avg(extract(year from age(birthdate))) > 60)	    
	    TypedQuery<Game> query = em.createQuery(
	            "select game from Game game "
	    		+"where game.id in ("
	    		+"select gameGamer.game.id from GameGamer gameGamer "
	    		+"where gameGamer.game.id = game.id "
	            +"group by gameGamer.game.id " 
	            +"having avg(YEAR(current_date) - YEAR(gameGamer.gamer.birthdate)) > :age)", 
	            Game.class);
	     List<Game> res = query.setParameter("age", age).getResultList();
	     return res;	
	}

	public List<GameWinnersMovesLessThen> getGameWinnersMoves(int moves){
//select game_id, count(*)as moves
//from game_gamer
//join move on game_gamer.id=game_gamer_id
//where is_winner
//group by game_id having count(*) < 5		
		TypedQuery<GameWinnersMovesLessThen> query = em.createQuery(
				"select gameGamer.game.id, count(*) as moves from Move "
				+"where gameGamer.is_winner "
				+"group by gameGamer.game.id having count(*) < :moves", 
				GameWinnersMovesLessThen.class);
		List<GameWinnersMovesLessThen> res = query.setParameter("moves", moves).getResultList();
		return res;
	}

	public List<Gamer> getGamerMovesInOneGameLessThen(int moves){
//select distinct gamer_id
//from game_gamer
//join move on game_gamer.id=game_gamer_id 
//group by game_id, gamer_id having count(*) < 4
		TypedQuery<Gamer> query = em.createQuery(
				"select distinct gameGamer.gamer.username from Move "
				+"group by gameGamer.gamer.id, gameGamer.game.id "
				+"having count(*) < :moves", 
				Gamer.class);
		List<Gamer> res = query.setParameter("moves", moves).getResultList();
		return res;
	}
	
	public List<GameAvgMovesOfGamers> getGameAvgMovesOfGamers(){
//select game_id, round(avg(moves), 1) 
//from (select game_id, gamer_id, count(*) moves
//from game_gamer
//join move
//on game_gamer.id=game_gamer_id 
//group by game_id, gamer_id
//order by game_id)
//group by game_id
		TypedQuery<GameAvgMovesOfGamers> query = em.createQuery(	
				"select gameGamer.game.id, avg(count(move.id)) "
				+"from Move move "
				+"group by gameGamer.gamer.id "
				,GameAvgMovesOfGamers.class);
		List<GameAvgMovesOfGamers> res = query.getResultList();
		return res;
	}
	
	
}























