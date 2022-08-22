--
-- PostgreSQL database dump
--

-- Dumped from database version 14.1
-- Dumped by pg_dump version 14.1

-- Started on 2022-01-11 01:03:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 211 (class 1259 OID 16405)
-- Name: routine_training; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.routine_training (
    id_routine uuid NOT NULL,
    id_training uuid NOT NULL,
    "order" integer
);


ALTER TABLE public.routine_training OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16395)
-- Name: routines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.routines (
    id uuid NOT NULL,
    name character varying,
    description character varying,
    user_id character varying
);


ALTER TABLE public.routines OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16395)
-- Name: routines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.summaries (
    routine uuid,
    mintime int8,
    maxtime int8
);


ALTER TABLE public.summaries OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16402)
-- Name: trainings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trainings (
    id uuid NOT NULL,
    name character varying,
    description character varying,
    trainer_id character varying,
    categories character varying[]
);


ALTER TABLE public.trainings OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16418)
-- Name: videos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.videos (
    id uuid NOT NULL,
    name character varying,
    ext character varying,
    buff bytea,
    training_id character varying
);


ALTER TABLE public.videos OWNER TO postgres;

--
-- TOC entry 3180 (class 2606 OID 32797)
-- Name: routine_training routine_training_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routine_training
    ADD CONSTRAINT routine_training_pkey PRIMARY KEY (id_routine, id_training);


--
-- TOC entry 3176 (class 2606 OID 16401)
-- Name: routines routines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routines
    ADD CONSTRAINT routines_pkey PRIMARY KEY (id);

--
-- TOC entry 3176 (class 2606 OID 16401)
-- Name: routines routines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.summaries
    ADD CONSTRAINT summaries_pkey PRIMARY KEY (routine);


--
-- TOC entry 3178 (class 2606 OID 16417)
-- Name: trainings trainings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trainings
    ADD CONSTRAINT trainings_pkey PRIMARY KEY (id);


--
-- TOC entry 3182 (class 2606 OID 16424)
-- Name: videos videos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_pkey PRIMARY KEY (id);


--
-- TOC entry 3183 (class 2606 OID 32830)
-- Name: routine_training routine_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routine_training
    ADD CONSTRAINT routine_fk FOREIGN KEY (id_routine) REFERENCES public.routines(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- TOC entry 3184 (class 2606 OID 32835)
-- Name: routine_training training_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routine_training
    ADD CONSTRAINT training_fk FOREIGN KEY (id_training) REFERENCES public.trainings(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;

--
-- TOC entry 3184 (class 2606 OID 32835)
-- Name: routine_training training_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.summaries
    ADD CONSTRAINT routine_fk FOREIGN KEY (routine) REFERENCES public.routines(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- TOC entry 3185 (class 2606 OID 32856)
-- Name: videos training_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT training_fk FOREIGN KEY (training_id) REFERENCES public.trainings(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


-- Completed on 2022-01-11 01:03:00

--
-- PostgreSQL database dump complete
--

INSERT INTO public.trainings VALUES (
    '80f029e4-acb0-4063-b01c-99a5027b4225',
    'EntrenamientoPrueba',
    'EntrenamientoPrueba description',
    'b8dc009f-e7b6-48b0-8685-63bbcf8153a9',
    ARRAY['Legs','Split Quats','Cardio']
);

INSERT INTO public.videos VALUES (
    'fd660598-7215-4cba-ad1b-e8550e93a4a2',
    'How to PROPERLY Bulgarian Split Squat To Grow Your Quads.mp4',
    'mp4',
    NULL,
    '80f029e4-acb0-4063-b01c-99a5027b4225'
);
