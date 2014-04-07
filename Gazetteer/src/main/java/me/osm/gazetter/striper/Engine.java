package me.osm.gazetter.striper;

import static me.osm.gazetter.utils.FileUtils.getFileIS;

import java.util.HashSet;

import me.osm.gazetter.striper.builders.Builder;
import me.osm.gazetter.striper.readers.PointsReader;
import me.osm.gazetter.striper.readers.RelationsReader;
import me.osm.gazetter.striper.readers.WaysReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {
	
	private static final Logger log = LoggerFactory.getLogger(Engine.class);
	
	public void filter(HashSet<String> drop, String datatDir, Builder... builders) {
		String nodes = datatDir + "/" + "nodes.osm";
		String ways = datatDir + "/" + "ways.osm";
		String rels = datatDir + "/" + "rels.osm";

		try {
			new RelationsReader(drop).read(getFileIS(rels), builders);
			log.info("First run: done relations.");
			for(Builder builder : builders) {
				builder.firstRunDoneRelations();
			}

			new WaysReader(drop).read(getFileIS(ways), builders);
			log.info("First run: done ways.");
			for(Builder builder : builders) {
				builder.firstRunDoneWays();
			}

			new PointsReader(drop).read(getFileIS(nodes), builders);
			log.info("First run: done nodes.");
			for(Builder builder : builders) {
				builder.firstRunDoneNodes();
			}

			new WaysReader(drop).read(getFileIS(ways), builders);
			log.info("Second run: done ways.");
			for(Builder builder : builders) {
				builder.secondRunDoneWays();
			}
			
			new RelationsReader(drop).read(getFileIS(rels), builders);
			log.info("Second run: done relations.");
			for(Builder builder : builders) {
				builder.secondRunDoneRelations();
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Parsing failed. Data dir: " + datatDir, e);
		}
	}
}
