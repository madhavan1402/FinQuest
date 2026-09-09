// src/components/mentor/MentorCanvas.jsx
// Direct Three.js WebGL canvas encapsulating avatar rendering, lighting, camera, and animations.
// Detects GLB availability and gracefully falls back to MentorFallback when assets are pending.

import React, { useRef, useEffect, useState } from 'react';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import MentorFallback from './MentorFallback';

export default function MentorCanvas({ gender = 'male', emotion = 'idle', isSpeaking = false }) {
  const containerRef = useRef(null);
  const [modelLoaded, setModelLoaded] = useState(false);
  const [loadFailed, setLoadFailed] = useState(false);

  // References for Three.js state
  const sceneRef = useRef(null);
  const rendererRef = useRef(null);
  const mixerRef = useRef(null);
  const actionsRef = useRef({});
  const activeActionRef = useRef(null);
  const reqIdRef = useRef(null);

  // Check prefers-reduced-motion
  const prefersReducedMotion = typeof window !== 'undefined' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  useEffect(() => {
    // Only attempt 3D if container exists
    const container = containerRef.current;
    if (!container) return;

    let isSubscribed = true;

    // 1. Initialize Scene, Camera, Renderer
    const width = container.clientWidth || 120;
    const height = container.clientHeight || 120;

    const scene = new THREE.Scene();
    sceneRef.current = scene;

    const camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 100);
    camera.position.set(0, 1.4, 2.2);

    let renderer;
    try {
      renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true, powerPreference: 'low-power' });
      renderer.setSize(width, height);
      renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      renderer.outputColorSpace = THREE.SRGBColorSpace;
      rendererRef.current = renderer;
      container.appendChild(renderer.domElement);
    } catch {
      // WebGL unsupported on machine
      setLoadFailed(true);
      return;
    }

    // 2. Lighting
    const ambientLight = new THREE.AmbientLight(0xffffff, 1.4);
    scene.add(ambientLight);

    const dirLight = new THREE.DirectionalLight(0xffffff, 1.8);
    dirLight.position.set(2, 4, 3);
    scene.add(dirLight);

    const rimLight = new THREE.PointLight(gender === 'female' ? 0xec4899 : 0x6366f1, 2, 10);
    rimLight.position.set(-2, 2, -2);
    scene.add(rimLight);

    // 3. Load GLB Model
    const modelPath = `/models/mentor_${gender}.glb`;
    const loader = new GLTFLoader();

    loader.load(
      modelPath,
      (gltf) => {
        if (!isSubscribed) return;
        const model = gltf.scene;
        model.position.set(0, 0, 0);
        scene.add(model);

        // Setup Animation Mixer
        if (gltf.animations && gltf.animations.length > 0) {
          const mixer = new THREE.AnimationMixer(model);
          mixerRef.current = mixer;

          const actions = {};
          gltf.animations.forEach((clip) => {
            const name = clip.name.toLowerCase();
            actions[name] = mixer.clipAction(clip);
          });
          actionsRef.current = actions;

          // Start idle
          const idleAction = actions['idle'] || Object.values(actions)[0];
          if (idleAction) {
            idleAction.play();
            activeActionRef.current = idleAction;
          }
        }

        setModelLoaded(true);
      },
      undefined,
      () => {
        // Asset not found or error loading GLB -> Fallback to 2D
        if (isSubscribed) {
          setLoadFailed(true);
        }
      }
    );

    // 4. Animation / Render Loop
    const clock = new THREE.Clock();
    const animate = () => {
      reqIdRef.current = requestAnimationFrame(animate);

      const delta = clock.getDelta();
      if (mixerRef.current && !prefersReducedMotion) {
        mixerRef.current.update(delta);
      }

      renderer.render(scene, camera);
    };
    animate();

    // 5. Resize Handler
    const handleResize = () => {
      if (!container || !renderer) return;
      const w = container.clientWidth || 120;
      const h = container.clientHeight || 120;
      camera.aspect = w / h;
      camera.updateProjectionMatrix();
      renderer.setSize(w, h);
    };
    window.addEventListener('resize', handleResize);

    // 6. Cleanup & Resource Disposal
    return () => {
      isSubscribed = false;
      window.removeEventListener('resize', handleResize);

      if (reqIdRef.current) cancelAnimationFrame(reqIdRef.current);

      if (mixerRef.current) {
        mixerRef.current.stopAllAction();
      }

      if (scene) {
        scene.traverse((obj) => {
          if (obj.geometry) obj.geometry.dispose();
          if (obj.material) {
            if (Array.isArray(obj.material)) {
              obj.material.forEach((m) => m.dispose());
            } else {
              obj.material.dispose();
            }
          }
        });
      }

      if (renderer) {
        renderer.dispose();
        if (renderer.domElement && renderer.domElement.parentNode) {
          renderer.domElement.parentNode.removeChild(renderer.domElement);
        }
      }
    };
  }, [gender, prefersReducedMotion]);

  // Handle emotion and speaking animation transitions
  useEffect(() => {
    const actions = actionsRef.current;
    if (!actions || !mixerRef.current) return;

    const targetClipName = isSpeaking
      ? 'talking'
      : emotion === 'celebrating'
      ? 'celebrating'
      : emotion === 'thinking'
      ? 'thinking'
      : emotion === 'encouraging'
      ? 'encouraging'
      : 'idle';

    const nextAction = actions[targetClipName] || actions['idle'];
    if (nextAction && nextAction !== activeActionRef.current) {
      if (activeActionRef.current) {
        activeActionRef.current.fadeOut(0.3);
      }
      nextAction.reset().fadeIn(0.3).play();
      activeActionRef.current = nextAction;
    }
  }, [emotion, isSpeaking]);

  // If 3D model failed to load or is not present in public/models/, use the 2D SVG/CSS fallback
  if (loadFailed || !modelLoaded) {
    return <MentorFallback gender={gender} emotion={emotion} isSpeaking={isSpeaking} />;
  }

  return <div ref={containerRef} className="w-28 h-28 mx-auto" aria-hidden="true" />;
}
